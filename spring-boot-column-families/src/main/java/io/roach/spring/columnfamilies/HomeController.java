package io.roach.spring.columnfamilies;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/")
public class HomeController {
    @PersistenceContext
    private EntityManager entityManager;

    @GetMapping(produces = {"text/plain", "application/json"})
    public String ping() {
        return "Hello from " +
                entityManager.createNativeQuery("select version()").getSingleResult();
    }

    @GetMapping
    public ResponseEntity<RepresentationModel<?>> index() {
        RepresentationModel<?> index = new RepresentationModel<>();

        index.add(linkTo(methodOn(Order1Controller.class)
                .findOrders())
                .withRel("orders"));

        index.add(linkTo(methodOn(Order2Controller.class)
                .findOrders())
                .withRel("orders"));

        return ResponseEntity.ok(index);
    }
}
