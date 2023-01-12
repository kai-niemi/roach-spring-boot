package io.roach.spring.inbox.subscriber;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/journal")
public class JournalController {
    @Autowired
    private RegistrationJournalRepository registrationJournalRepository;

    @Autowired
    private PagedResourcesAssembler<RegistrationJournal> pagedResourcesAssembler;

    @GetMapping("/registration-events")
    public HttpEntity<PagedModel<EntityModel<RegistrationJournal>>> listRegistrationEvents(
            @PageableDefault(size = 5, direction = Sort.Direction.ASC) Pageable page,
            @RequestParam(value = "jurisdiction", defaultValue = "mga") String jurisdiction) {
        return ResponseEntity
                .ok(pagedResourcesAssembler.toModel(
                        registrationJournalRepository.findEventsPageWithJurisdiction(page, jurisdiction),
                        EntityModel::of));
    }
}
