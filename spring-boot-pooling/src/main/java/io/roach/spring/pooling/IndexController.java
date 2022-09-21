package io.roach.spring.pooling;

import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(value = "/")
public class IndexController {

    @GetMapping
    public ResponseEntity<RepresentationModel<?>> index() {
        RepresentationModel<?> index = new RepresentationModel<>();

        index.add(linkTo(methodOn(AccountController.class)
                .findAll(PageRequest.of(0, 16)))
                .withRel("accounts")
                .withTitle("Paginated collection of registered accounts"));

        index.add(linkTo(methodOn(AccountController.class)
                .longPoll(15))
                .withRel("accounts-poll")
                .withTitle("Claim and hold connection for time period"));

        index.add(linkTo(methodOn(AdminController.class)
                .index())
                .withRel("admin")
                .withTitle("Admin and metadata"));

        index.add(linkTo(methodOn(WorkloadController.class)
                .submitForm(null))
                .withRel("workload")
                .withTitle("Workload controller"));

        return ResponseEntity.ok(index);
    }
}


