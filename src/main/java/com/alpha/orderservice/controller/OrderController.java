package com.alpha.orderservice.controller;

import com.alpha.orderservice.dto.OrderDto;
import com.alpha.orderservice.dto.ProductDto;
import com.alpha.orderservice.dto.ProductLineDto;
import com.alpha.orderservice.input.NewOrderInput;
import com.alpha.orderservice.input.UpdateOrderInput;
import com.alpha.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
public class OrderController {

    private final OrderService orderService;

    @MutationMapping
    public OrderDto createOrder(@Argument(name = "order") NewOrderInput newOrderInput) {
        return orderService.createOrder(newOrderInput);
    }

    @MutationMapping
    OrderDto updateOrder(@Argument(name = "order") UpdateOrderInput updateOrderInput) {
        return orderService.updateOrder(updateOrderInput);
    }

    @MutationMapping
    String deleteOrder(@Argument long orderId) {
        return orderService.deleteOrder(orderId);
    }

    @QueryMapping
    List<OrderDto> orders() {
        return orderService.getAllOrders();
    }

    @QueryMapping
    OrderDto getOrderById(@Argument long orderId) {
        return orderService.getOrderById(orderId);
    }

    @QueryMapping
    List<OrderDto> getOrdersForUser(@Argument long userId) {
        return orderService.getOrdersForUser(userId);
    }

    @QueryMapping
    List<ProductLineDto> getProductsForOrder(@Argument long orderId) {
        System.out.println("Controller called");
        return orderService.getProductsForOrder(orderId);
    }
}
