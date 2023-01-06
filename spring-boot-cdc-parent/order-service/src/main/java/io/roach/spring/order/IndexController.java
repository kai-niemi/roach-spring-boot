package io.roach.spring.order;

import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.roach.spring.order.product.ProductController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(value = "/order-service")
public class IndexController {
    @GetMapping("/")
    public ResponseEntity<RepresentationModel<?>> getApiIndex() {
        RepresentationModel<?> index = new RepresentationModel<>();

        index.add(WebMvcLinkBuilder
                .linkTo(methodOn(ProductController.class)
                        .findAll(PageRequest.of(0, 5)))
                .withRel(LinkRelations.PRODUCTS_REL)
                .withTitle("Product collection resource"));

        return ResponseEntity.ok(index);
    }
}
