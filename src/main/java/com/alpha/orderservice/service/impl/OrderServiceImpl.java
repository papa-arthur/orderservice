package com.alpha.orderservice.service.impl;

import com.alpha.orderservice.dto.OrderDto;
import com.alpha.orderservice.dto.ProductDto;
import com.alpha.orderservice.dto.ProductLineDto;
import com.alpha.orderservice.entity.Order;
import com.alpha.orderservice.entity.Product;
import com.alpha.orderservice.entity.ProductLine;
import com.alpha.orderservice.entity.User;
import com.alpha.orderservice.exception.InsufficientStockException;
import com.alpha.orderservice.exception.OrderNotFoundException;
import com.alpha.orderservice.exception.ProductNotFoundException;
import com.alpha.orderservice.exception.UserNotFoundException;
import com.alpha.orderservice.input.NewOrderInput;
import com.alpha.orderservice.input.UpdateOrderInput;
import com.alpha.orderservice.repository.OrderRepository;
import com.alpha.orderservice.repository.ProductLineRepository;
import com.alpha.orderservice.repository.ProductRepository;
import com.alpha.orderservice.repository.UserRepository;
import com.alpha.orderservice.service.EntityToDtoMapper;
import com.alpha.orderservice.service.OrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductLineRepository productLineRepository;
    private final EntityToDtoMapper mapper;

    @Override
    @Transactional
    public OrderDto createOrder(NewOrderInput newOrderInput) {
        User user = userRepository.findById(newOrderInput.getUserId())
                .orElseThrow(() -> new UserNotFoundException(
                        String.format("Failed to create order! User with id '%d' does not exist", newOrderInput.getUserId())));

        Order newOrder = Order.builder()
                .user(user)
                .build();

        List<ProductLine> productLines = newOrderInput.getProductLines().stream()
                .map(productLineInput -> {
                    Product dbProduct = productRepository.findById(productLineInput.getProductId())
                            .orElseThrow(() -> new ProductNotFoundException(String.format("Failed to create order! Product with id: %d could not be not found", productLineInput.getProductId())));

                    if (productLineInput.getQuantity() < 1)
                        throw new IllegalArgumentException("Failed to create order! Product quantity must not be at least 1");

                    if (dbProduct.getStock() < productLineInput.getQuantity())
                        throw new InsufficientStockException(String.format("Failed to create order! Product with id: '%d' does not have enough stock. " +
                                " Available stock: %d", productLineInput.getProductId(), dbProduct.getStock()));

                    dbProduct.setStock(dbProduct.getStock() - productLineInput.getQuantity());

                    return ProductLine.builder()
                            .quantity(productLineInput.getQuantity())
                            .product(dbProduct)
                            .order(newOrder)
                            .build();

                })
                .toList();

        newOrder.setProductLines(productLines);
        Order savedOrder = orderRepository.save(newOrder);
        return mapper.entityToDto(savedOrder);
    }

    @Override
    public OrderDto getOrderById(long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new OrderNotFoundException(String.format("Order with id: %d could not be found", orderId)));
        return mapper.entityToDto(order);
    }

    @Override
    public List<OrderDto> getOrdersForUser(long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException(String.format("User with id '%d' could not be found", userId))
        );

        return mapper.entityToOrderDtoList(user.getOrders());
    }

    @Override
    public List<OrderDto> getAllOrders() {
        return mapper.entityToOrderDtoList(orderRepository.findAll());
    }

    @Override
    public List<ProductLineDto> getProductsForOrder(@Argument(name = "orderId") long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new OrderNotFoundException(String.format("Order with id: %d could not be found", orderId)));
        return mapper.entityToProductLineDtoList(order.getProductLines());
    }

    @Override
    @Transactional
    public OrderDto updateOrder(UpdateOrderInput orderUpdateInput) {

        Order order = orderRepository.findById(orderUpdateInput.getOrderId()).orElseThrow(
                () -> new OrderNotFoundException(String.format("Order with id: %d could not be not found", orderUpdateInput.getOrderId())));

        List<ProductLine> productLinesUpdates = new ArrayList<>();

        for (var input : orderUpdateInput.getProductLines()) {

            if (input.getId() > 0) {
                ProductLine dbProductLine = productLineRepository.findById(input.getId()).orElseThrow(
                        () -> new ProductNotFoundException(
                                String.format("Failed  to update order! Product with id: %d could not be not found", input.getProductId())));
                Product product = dbProductLine.getProduct();

                int newQuantity = input.getQuantity() - dbProductLine.getQuantity();
                if (newQuantity > 0) {
                    dbProductLine.setQuantity(newQuantity + dbProductLine.getQuantity());
                    product.setStock(product.getStock() - newQuantity);

                } else if (newQuantity < 0) {

                    newQuantity = Math.abs(newQuantity);
                    dbProductLine.setQuantity(dbProductLine.getQuantity() - newQuantity);
                    product.setStock(product.getStock() + newQuantity);

                }

                var updatedProductLine =productLineRepository.save(dbProductLine);

                productLinesUpdates.add(updatedProductLine);

            } else if (input.getId() == 0) {
                Product dbProduct = productRepository.findById(input.getProductId())
                        .orElseThrow(() -> new ProductNotFoundException(
                                String.format("Failed to create order! Product with id: %d could not be not found", input.getProductId())));

                if (input.getQuantity() < 1)
                    throw new IllegalArgumentException("Failed to create order! Product quantity must not be at least 1");

                if (dbProduct.getStock() < input.getQuantity())
                    throw new InsufficientStockException(String.format("Failed to create order! Product with id: '%d' does not have enough stock. " +
                            "Available stock: %d", input.getProductId(), dbProduct.getStock()));

                ProductLine newProductLine = ProductLine.builder()
                        .quantity(input.getQuantity())
                        .order(order)
                        .product(dbProduct).build();
                productLinesUpdates.add(newProductLine);
            }
        }

        order.setProductLines(productLinesUpdates);
        return mapper.entityToDto(orderRepository.save(order));
    }

    @Override
    public String deleteOrder(long orderId) {
        if (orderRepository.existsById(orderId)) {
            orderRepository.deleteById(orderId);
            return String.format("Order with id: %d deleted successfully", orderId);
        }
        throw new OrderNotFoundException(String.format("Deletion failed! Order with id: %d could not be found", orderId));
    }


}
