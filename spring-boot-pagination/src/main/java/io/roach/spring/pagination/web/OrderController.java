package io.roach.spring.pagination.web;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.roach.spring.annotations.TransactionBoundary;
import io.roach.spring.pagination.domain.Order;
import io.roach.spring.pagination.domain.OrderItem;
import io.roach.spring.pagination.repository.OrderRepository;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(value = "/order")
public class OrderController {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PagedResourcesAssembler<Order> pagedResourcesAssembler;

    @GetMapping
    public ResponseEntity<RepresentationModel<?>> index() {
        RepresentationModel<?> index = new RepresentationModel<>();

        index.add(linkTo(methodOn(getClass())
                .listOrders(PageRequest.of(0, 5)))
                .withRel(LinkRels.ORDERS_REL));

        return ResponseEntity.ok(index);
    }

    @GetMapping("/")
    @TransactionBoundary(followerRead = true)
    public HttpEntity<PagedModel<OrderModel>> listOrders(
            @PageableDefault(size = 5, direction = Sort.Direction.ASC) Pageable page) {
        return ResponseEntity
                .ok(pagedResourcesAssembler.toModel(
                        orderRepository.findAll(page), orderModelAssembler()));
    }

    @GetMapping(value = "/{id}")
    @TransactionBoundary(readOnly = true)
    @Retryable
    public HttpEntity<OrderModel> getOrder(@PathVariable("id") UUID orderId) {
        return ResponseEntity.ok(orderModelAssembler().toModel(orderRepository.getById(orderId)));
    }

    @GetMapping("/{id}/customer")
    @TransactionBoundary(followerRead = true)
    public HttpEntity<PagedModel<OrderModel>> listOrdersByCustomer(
            @PageableDefault(size = 5, direction = Sort.Direction.ASC) Pageable page,
            @PathVariable("id") UUID customerId) {
        return ResponseEntity
                .ok(pagedResourcesAssembler.toModel(
                        orderRepository.findOrdersByCustomerId(page, customerId), orderModelAssembler()));
    }

    @GetMapping("/{id}/product")
    @TransactionBoundary(followerRead = true)
    public HttpEntity<PagedModel<OrderModel>> listOrdersByProduct(
            @PageableDefault(size = 5, direction = Sort.Direction.ASC) Pageable page,
            @PathVariable("id") UUID productId) {
        return ResponseEntity
                .ok(pagedResourcesAssembler.toModel(
                        orderRepository.findOrdersByProductId(page, productId), orderModelAssembler()));
    }

    @GetMapping(value = "/{id}/items")
    @TransactionBoundary(readOnly = true)
    public HttpEntity<CollectionModel<OrderItemModel>> getOrderItems(@PathVariable("id") UUID orderId) {
        Order order = orderRepository.getById(orderId);
        return ResponseEntity.ok(orderItemModelAssembler().toCollectionModel(order.getOrderItems()));
    }

    private RepresentationModelAssembler<Order, OrderModel> orderModelAssembler() {
        return (entity) -> {
            OrderModel model = new OrderModel();
            model.setTotalPrice(entity.getTotalPrice());

            // Zoom
            model.setOrderItems(orderItemModelAssembler().toCollectionModel(entity.getOrderItems()));
            model.add(linkTo(methodOn(OrderController.class)
                    .getOrderItems(entity.getId())
            ).withRel(LinkRels.ORDERS_ITEMS_REL));

            model.add(linkTo(methodOn(OrderController.class)
                    .getOrder(entity.getId())
            ).withRel(IanaLinkRelations.SELF));

            model.add(linkTo(methodOn(CustomerController.class)
                    .getCustomer(entity.getCustomer().getId())
            ).withRel(LinkRels.CUSTOMER_REL));

            return model;
        };
    }

    private RepresentationModelAssembler<OrderItem, OrderItemModel> orderItemModelAssembler() {
        return (entity) -> {
            OrderItemModel model = new OrderItemModel();
            model.setTotalCost(entity.totalCost());
            model.setQuantity(entity.getQuantity());
            model.setUnitPrice(entity.getUnitPrice());

            model.add(linkTo(methodOn(ProductController.class)
                    .getProduct(entity.getProduct().getId())
            ).withRel(LinkRels.PRODUCT_REL));

            return model;
        };
    }
}
