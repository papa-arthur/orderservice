package com.alpha.orderservice.service;

import com.alpha.orderservice.dto.OrderDto;
import com.alpha.orderservice.dto.ProductDto;
import com.alpha.orderservice.dto.ProductLineDto;
import com.alpha.orderservice.input.NewOrderInput;
import com.alpha.orderservice.input.UpdateOrderInput;

import java.util.List;

public interface OrderService {


    OrderDto createOrder(NewOrderInput newOrderInput);

    OrderDto getOrderById(long orderId);

    List<OrderDto> getOrdersForUser(long userId);

    List<OrderDto> getAllOrders();

    List<ProductLineDto> getProductsForOrder(long orderId);

    OrderDto updateOrder(UpdateOrderInput orderUpdateInput);

    String deleteOrder(long orderId);
}
