package io.roach.spring.columnfamilies;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/order/v2")
public class Order2Controller extends AbstractOrderController<Order2> {
}
