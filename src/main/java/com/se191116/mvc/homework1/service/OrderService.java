package com.se191116.mvc.homework1.service;

import com.se191116.mvc.homework1.dto.CreateOrderItemRequest;
import com.se191116.mvc.homework1.dto.CreateOrderRequest;
import com.se191116.mvc.homework1.dto.OrderItemResponse;
import com.se191116.mvc.homework1.dto.OrderResponse;
import com.se191116.mvc.homework1.entity.CustomerOrder;
import com.se191116.mvc.homework1.entity.OrderItem;
import com.se191116.mvc.homework1.entity.OrderStatus;
import com.se191116.mvc.homework1.entity.Product;
import com.se191116.mvc.homework1.entity.User;
import com.se191116.mvc.homework1.exception.BadRequestException;
import com.se191116.mvc.homework1.exception.ResourceNotFoundException;
import com.se191116.mvc.homework1.repository.OrderItemRepository;
import com.se191116.mvc.homework1.repository.OrderRepository;
import com.se191116.mvc.homework1.repository.ProductRepository;
import com.se191116.mvc.homework1.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public OrderService(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            ProductRepository productRepository,
            UserRepository userRepository
    ) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public OrderResponse createOrder(String username, CreateOrderRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        CustomerOrder order = new CustomerOrder();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setTotalMoney(BigDecimal.ZERO);
        CustomerOrder savedOrder = orderRepository.save(order);

        BigDecimal total = BigDecimal.ZERO;
        for (CreateOrderItemRequest itemRequest : request.items()) {
            Product product = productRepository.findById(itemRequest.productId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + itemRequest.productId()));

            OrderItem item = new OrderItem();
            item.setOrder(savedOrder);
            item.setProduct(product);
            item.setQuantity(itemRequest.quantity());
            item.setPriceBuy(product.getPrice());
            orderItemRepository.save(item);
            savedOrder.getItems().add(item);

            BigDecimal lineTotal = product.getPrice().multiply(BigDecimal.valueOf(itemRequest.quantity()));
            total = total.add(lineTotal);
        }

        if (savedOrder.getItems().isEmpty()) {
            throw new BadRequestException("Order must contain at least one item");
        }

        savedOrder.setTotalMoney(total);
        return toResponse(orderRepository.save(savedOrder));
    }

    public List<OrderResponse> getMyOrders(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        return orderRepository.findByUserOrderByCreatedDateDesc(user).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public OrderResponse updateStatus(Long orderId, OrderStatus status) {
        CustomerOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));
        order.setStatus(status);
        return toResponse(orderRepository.save(order));
    }

    private OrderResponse toResponse(CustomerOrder order) {
        List<OrderItemResponse> items = order.getItems().stream()
                .map(item -> new OrderItemResponse(
                        item.getId(),
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getPriceBuy(),
                        item.getPriceBuy().multiply(BigDecimal.valueOf(item.getQuantity()))
                ))
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getUser().getUsername(),
                order.getCreatedDate(),
                order.getStatus(),
                order.getTotalMoney(),
                items
        );
    }
}
