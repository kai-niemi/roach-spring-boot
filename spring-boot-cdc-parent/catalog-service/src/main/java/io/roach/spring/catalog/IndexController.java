package io.roach.spring.catalog;

import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import io.roach.spring.catalog.product.ProductController;
import io.roach.spring.catalog.scheduler.SchedulingController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class IndexController {
    @GetMapping("/")
    public RedirectView getHome() {
        return new RedirectView("catalog-service");
    }

    @GetMapping("/catalog-service")
    public ResponseEntity<RepresentationModel<?>> getApiIndex() {
        RepresentationModel<?> index = new RepresentationModel<>();

        index.add(WebMvcLinkBuilder
                .linkTo(methodOn(ProductController.class)
                        .findAll(PageRequest.of(0, 5)))
                .withRel(LinkRelations.PRODUCTS_REL)
                .withTitle("Product collection resource"));

        index.add(WebMvcLinkBuilder
                .linkTo(methodOn(SchedulingController.class)
                        .getShedulingStatus())
                .withRel(LinkRelations.SCHEDULING_REL)
                .withTitle("Scheduling resource"));

        return ResponseEntity.ok(index);
    }
}
