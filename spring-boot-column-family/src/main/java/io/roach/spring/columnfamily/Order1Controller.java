package io.roach.spring.columnfamily;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/order/v1")
public class Order1Controller extends AbstractOrderController<Order1> {
}