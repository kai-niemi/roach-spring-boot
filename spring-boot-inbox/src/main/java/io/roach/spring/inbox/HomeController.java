package io.roach.spring.inbox;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.roach.spring.inbox.producer.RegistrationController;
import io.roach.spring.inbox.subscriber.RegistrationJournal;
import io.roach.spring.inbox.subscriber.RegistrationJournalRepository;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/")
public class HomeController {
    @Autowired
    private RegistrationJournalRepository registrationJournalRepository;

    @GetMapping("/list")
    public CollectionModel<RegistrationJournal> listEvents(@RequestParam(value = "jur", defaultValue = "mga") String jur) {
        return CollectionModel.of(registrationJournalRepository.findEventsWithJurisdiction(jur));
    }

    @GetMapping
    public ResponseEntity<RepresentationModel<?>> index() {
        RepresentationModel<?> index = new RepresentationModel<>();

        index.add(linkTo(methodOn(RegistrationController.class)
                .getTemplate())
                .withRel("template"));

        return ResponseEntity.ok(index);
    }
}
