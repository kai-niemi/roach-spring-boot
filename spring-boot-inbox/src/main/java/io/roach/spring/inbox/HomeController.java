package io.roach.spring.inbox;

import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.roach.spring.inbox.producer.RegistrationController;
import io.roach.spring.inbox.subscriber.JournalController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/")
public class HomeController {
    @GetMapping
    public ResponseEntity<RepresentationModel<?>> index() {
        RepresentationModel<?> index = new RepresentationModel<>();

        index.add(linkTo(methodOn(RegistrationController.class)
                .getFormTemplate())
                .withRel("registration-form"));

        index.add(linkTo(methodOn(JournalController.class)
                .listRegistrationEvents(PageRequest.of(0, 5), "mga"))
                .withRel("journal"));

        return ResponseEntity.ok(index);
    }
}
