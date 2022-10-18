package io.roach.spring.idempotency.domain.transfer;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectUpdateSemanticsDataAccessException;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.JdbcUpdateAffectedIncorrectNumberOfRowsException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.roach.spring.idempotency.domain.account.AccountEntity;
import io.roach.spring.idempotency.domain.account.AccountService;
import io.roach.spring.idempotency.domain.poe.Checksum;
import io.roach.spring.idempotency.domain.poe.TransactionTagRepository;
import io.roach.spring.idempotency.domain.transaction.TransactionCollectionTag;
import io.roach.spring.idempotency.domain.transaction.TransactionEntity;
import io.roach.spring.idempotency.domain.transaction.TransactionEntityAssembler;
import io.roach.spring.idempotency.domain.transaction.TransactionService;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(path = "/transfer")
public class TransferController {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionTagRepository poeTagRepository;

    @Autowired
    private TransactionEntityAssembler transactionEntityAssembler;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping
    public ResponseEntity<RepresentationModel<?>> index() {
        RepresentationModel<?> index = new RepresentationModel<>();
        index.add(linkTo(methodOn(getClass()).getTransferRequestTemplate()).withRel("transfer-form")
                .withTitle("Transfer form template"));
        return ResponseEntity.ok(index);
    }

    @GetMapping(value = "/form")
    public ResponseEntity<TransferRequest> getTransferRequestTemplate() {
        TransferRequest.Builder builder = TransferRequest.builder();

        accountService.findAll(PageRequest.ofSize(4)).forEach(account -> {
            builder.addLeg()
                    .withId(account.getId())
                    .withAmount(BigDecimal.ZERO)
                    .then();
        });

        TransferRequest request = builder.build();
        request.add(linkTo(methodOn(getClass()).signTransferRequest(request)).withRel("transfer-form-signature")
                .withTitle("Sign request with current account states"));
        request.add(
                linkTo(methodOn(getClass()).submitTransferRequest(UUID.randomUUID(), request)).withRel("transfer-once")
                        .withTitle("Submit transfer request using POE tag"));

        return ResponseEntity.ok(request);
    }

    @GetMapping(value = "/signature")
    public ResponseEntity<RepresentationModel<?>> signTransferRequest(@RequestBody TransferRequest intent) {
        if (intent.getLegs().size() < 2) {
            throw new IllegalTransferException("Request must have at least two legs");
        }

        TransferRequest request = copyRequest(intent);

        String token = generateToken(request);

        RepresentationModel<?> model = new RepresentationModel<>();
        model.add(linkTo(methodOn(TransferController.class).submitSignedTransferRequest(token, intent)).withRel(
                "transfer").withTitle("Submit transfer request with one time token"));

        return ResponseEntity.status(HttpStatus.OK) // Should be 100 continue but it blocks postman
                .header("X-transfer", token).header("Cache-Control", "no-cache, no-store, must-revalidate")
                .header("Pragma", "no-cache").body(model);
    }

    private TransferRequest copyRequest(TransferRequest request) {
        TransferRequest.Builder builder = TransferRequest.builder();
        request.getLegs().forEach(accountLeg -> {
            AccountEntity account = accountService.findById(accountLeg.getId());
            builder.addLeg().withId(accountLeg.getId()).withBalance(account.getBalance()).then();
        });
        return builder.build();
    }

    private String generateToken(TransferRequest template) {
        try {
            String payload = objectMapper.writer().writeValueAsString(template);
            return Checksum.sha256().encodeToHex(payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping(value = "/signature/{token}")
    public ResponseEntity<CollectionModel<EntityModel<TransactionEntity>>> submitSignedTransferRequest(
            @PathVariable("token") String token, @RequestBody TransferRequest request) {
        // Validate
        request.getLegs().forEach(accountLeg -> {
            if (accountLeg.getAmount() == null) {
                throw new IllegalTransferException("Account leg amount required: " + accountLeg.getId());
            }
        });

        // Pre-condition check for idempotency
        String freshToken = generateToken(copyRequest(request));
        if (!token.equals(freshToken)) {
            // Could also fetch previous transactions tagged with token and return OK instead of PRECONDITION_FAILED
            throw new ExpiredTokenException("Transfer token (nonce) expired or invalid: " + token);
        }

        // Proceed
        List<TransactionEntity> entities = transactionService.createTransactions(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(transactionEntityAssembler.toCollectionModel(entities));
    }

    @PostMapping(value = "/{tag}")
    public ResponseEntity<CollectionModel<EntityModel<TransactionEntity>>> submitTransferRequest(
            @PathVariable("tag") UUID tag, @RequestBody TransferRequest request) {
        // Pre-condition check for idempotency
        Optional<TransactionCollectionTag> poeTag = poeTagRepository.findById(tag);
        if (poeTag.isPresent()) {
            // Lookup original response
            List<TransactionEntity> entities = poeTag.get().getBody();
            return ResponseEntity.status(HttpStatus.OK).header("POE-Link", tag.toString())
                    .body(transactionEntityAssembler.toCollectionModel(entities));
        }

        // Validate
        request.getLegs().forEach(accountLeg -> {
            if (accountLeg.getAmount() == null) {
                throw new IllegalTransferException("Account leg amount required: " + accountLeg.getId());
            }
        });

        // Proceed with transfer

        List<TransactionEntity> entities = transactionService.createTransactions(request);

        // Create POE tag for current request with serialized response

        TransactionCollectionTag newTag = new TransactionCollectionTag();
        newTag.setCreatedAt(LocalDateTime.now());
        newTag.setId(tag);
        newTag.setAggregateType(TransferRequest.class.getSimpleName());
        newTag.setUri(ServletUriComponentsBuilder.fromCurrentRequestUri().buildAndExpand().toUriString());
        newTag.setBody(entities);
        poeTagRepository.save(newTag);

        return ResponseEntity.status(HttpStatus.CREATED).header("POE-Link", newTag.getId().toString())
                .body(transactionEntityAssembler.toCollectionModel(entities));
    }

    @GetMapping(value = "/{tag}")
    public ResponseEntity<CollectionModel<EntityModel<TransactionEntity>>> getTransferOutcome(
            @PathVariable("tag") UUID tag) {
        // Pre-condition check for idempotency
        Optional<TransactionCollectionTag> poeTag = poeTagRepository.findById(tag);
        if (poeTag.isPresent()) {
            // Lookup original response
            List<TransactionEntity> entities = poeTag.get().getBody();
            return ResponseEntity.status(HttpStatus.OK).header("POE-Link", tag.toString())
                    .body(transactionEntityAssembler.toCollectionModel(entities));
        }

        throw new UnknownTokenException("No such token: " + tag);
    }

    @PatchMapping(value = "/{tag}")
    public ResponseEntity<EntityModel<TransactionCollectionTag>> updateTransferToken(
            @PathVariable("tag") UUID tag) {
        // Pre-condition check for idempotency
        Optional<TransactionCollectionTag> poeTag = poeTagRepository.findById(tag);
        if (poeTag.isPresent()) {
            TransactionCollectionTag unboxedTag = poeTag.get();
            if (poeTagRepository.increaseTTLInterval(unboxedTag.getId()) != 1) {
                throw new IncorrectUpdateSemanticsDataAccessException("POE tag rows affected was not 1");
            }
            return ResponseEntity.ok()
                    .header("POE-Link", tag.toString())
                    .body(EntityModel.of(unboxedTag));
        }

        throw new UnknownTokenException("No such token: " + tag);
    }
}
