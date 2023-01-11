package io.roach.spring.inbox.producer;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.roach.spring.inbox.event.RegistrationEvent;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(value = "/register")
public class RegistrationController {
    @Autowired
    private RegistrationEventProducer eventProducer;

    @PostMapping(value = "/")
    public ResponseEntity<EntityModel<RegistrationEvent>> sendMessage(
            @RequestBody RegistrationEvent registrationEvent) {
        if (registrationEvent.getId() == null) {
            registrationEvent.setId(UUID.randomUUID());
        }
        eventProducer.sendMessage(registrationEvent);
        return ResponseEntity.ok(toModel(registrationEvent));
    }

    @GetMapping("/template")
    public ResponseEntity<EntityModel<RegistrationEvent>> getTemplate() {
        RegistrationEvent event = RegistrationEvent.builder()
                .withJurisdiction("MGA")
                .withEmail("user@email.com")
                .withName("User")
                .build();
        return ResponseEntity.ok(EntityModel.of(event)
                .add(linkTo(methodOn(getClass()).sendMessage(null))
                        .withRel("register")));
    }


    private EntityModel<RegistrationEvent> toModel(RegistrationEvent event) {
        return EntityModel.of(event)
                .add(linkTo(methodOn(getClass()).sendMessage(event)).withSelfRel());
    }
}
