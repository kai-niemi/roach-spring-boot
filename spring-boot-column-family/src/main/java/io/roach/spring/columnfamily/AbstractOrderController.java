package io.roach.spring.columnfamily;

import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.afford;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@Transactional(propagation = Propagation.NEVER)
public abstract class AbstractOrderController<T extends AbstractOrder> {
    protected final Class<T> orderClass;

    @Autowired
    private OrderService orderService;

    public AbstractOrderController() {
        this.orderClass = (Class<T>) ((ParameterizedType)
                getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    @GetMapping
    public CollectionModel<T> findOrders() {
        CollectionModel<T> model = CollectionModel.of(new ArrayList<>(orderService.findAllOrders(orderClass)));
        return model
                .add(linkTo(methodOn(getClass())
                        .deleteOrders())
                        .withRel("clear"))
                .add(linkTo(methodOn(getClass())
                        .getOrderTemplate())
                        .withRel("template")
                        .andAffordance(afford(methodOn(getClass()).submitOrder(null))));
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<EntityModel<T>> getOrderById(@PathVariable("id") Long id) {
        Assert.isTrue(!TransactionSynchronizationManager.isActualTransactionActive(), "Expected no transaction!");
        T order = orderService.getOrderById(orderClass, id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(toModel(order));
    }

    @DeleteMapping(value = "/{id}")
    public HttpEntity<Void> deleteOrderById(@PathVariable("id") Long id) {
        Assert.isTrue(!TransactionSynchronizationManager.isActualTransactionActive(), "Expected no transaction!");
        orderService.deleteOne(orderClass, id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(produces = APPLICATION_JSON_VALUE)
    public HttpEntity<Void> deleteOrders() {
        orderService.deleteAll(orderClass);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/template")
    public ResponseEntity<EntityModel<T>> getOrderTemplate() {
        Address address1 = new Address();
        address1.setAddress1("Street 1.1");
        address1.setAddress2("Street 1.2");
        address1.setCity("City 1");
        address1.setPostcode("Code 1");
        address1.setCountry("Country 1");

        Address address2 = new Address();
        address2.setAddress1("Street 2.1");
        address2.setAddress2("Street 2.2");
        address2.setCity("City 2");
        address2.setPostcode("Code 2");
        address2.setCountry("Country 2");

        T template;
        try {
            template = orderClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException();
        }

        template.setBillToFirstName("Firstname");
        template.setBillToLastName("Lastname");
        template.setDeliverToFirstName("Firstname");
        template.setDeliverToLastName("Lastname");
        template.setDatePlaced(LocalDate.now());
        template.setBillAddress(address1);
        template.setDeliveryAddress(address2);

        template.setTotalPrice(BigDecimal.ZERO);
        template.setId(1L);

        return ResponseEntity.ok(EntityModel.of(template)
                .add(linkTo(methodOn(getClass()).submitOrder(null))
                        .withRel("orders")));
    }

    @PostMapping
    public ResponseEntity<EntityModel<T>> submitOrder(@RequestBody T order) {
        order.setOrderStatus(OrderStatus.PLACED);
        order.setDatePlaced(LocalDate.now());

        order = orderService.placeOrder(order);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toModel(order));
    }

    private EntityModel<T> toModel(T order) {
        return EntityModel.of(order)
                .add(linkTo(methodOn(getClass()).getOrderById(order.getId())).withSelfRel()
                                .andAffordance(
                                        afford(methodOn(getClass()).updateOrderStatus(order.getId(), 5)))
                                .withRel("status")
                                .andAffordance(afford(methodOn(getClass()).updateOrderPrice(order.getId(), BigDecimal.ZERO, 5)))
                                .withRel("price")
                                .andAffordance(afford(methodOn(getClass()).deleteOrderById(order.getId()))),
                        linkTo(methodOn(getClass()).findOrders())
                                .withRel("orders"));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<EntityModel<T>> updateOrderStatus(@PathVariable("id") Long id,
                                                            @RequestParam(value = "delay", defaultValue = "0") long commitDelay) {
        Assert.isTrue(!TransactionSynchronizationManager.isActualTransactionActive(), "Expected no transaction!");

        orderService.updateOrderStatus(orderClass, id, commitDelay);

        return getOrderById(id);
    }

    @PutMapping("/{id}/price")
    public ResponseEntity<EntityModel<T>> updateOrderPrice(@PathVariable("id") Long id,
                                                           @RequestParam(value = "price", defaultValue = "0") BigDecimal price,
                                                           @RequestParam(value = "delay", defaultValue = "0") long commitDelay) {
        Assert.isTrue(!TransactionSynchronizationManager.isActualTransactionActive(), "Expected no transaction!");

        orderService.updateOrderPrice(orderClass, id, price, commitDelay);

        return getOrderById(id);
    }
}
