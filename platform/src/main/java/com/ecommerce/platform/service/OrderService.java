package com.ecommerce.platform.service;

import com.ecommerce.platform.model.*;
import com.ecommerce.platform.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ecommerce.platform.dto.OrderResponseDTO;
import com.ecommerce.platform.dto.OrderItemDTO;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, 
                        OrderItemRepository orderItemRepository, 
                        CartRepository cartRepository, 
                        UserRepository userRepository,
                        ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

   @Transactional
public OrderResponseDTO checkout(String username) {
    User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));

    List<CartItem> cartItems = cartRepository.findByUser(user);
    if (cartItems.isEmpty()) {
        throw new RuntimeException("Cart is empty");
    }

    // 1. Create the Order Parent
    Order order = new Order();
    order.setUser(user);
    order.setOrderDate(LocalDateTime.now());
    order.setStatus("COMPLETED");
    
    // Calculate total
    double total = cartItems.stream()
            .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
            .sum();
    order.setTotalAmount(total);

    // 2. Save the Order FIRST to get an ID
    Order savedOrder = orderRepository.save(order);

    // 3. Create and Link Items
    List<OrderItem> orderItemsList = new ArrayList<>();
    for (CartItem cartItem : cartItems) {
        Product product = cartItem.getProduct();
        
        // Subtract stock
        product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
        productRepository.save(product);

        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(savedOrder); // Link Item -> Order
        orderItem.setProduct(product);
        orderItem.setQuantity(cartItem.getQuantity());
        orderItem.setPriceAtPurchase(product.getPrice());
        
        orderItemsList.add(orderItem);
    }

    // 4. CRITICAL: Link Order -> Items (This is what fixes the empty list!)
    savedOrder.setOrderItems(orderItemsList);

    // 5. Save all items
    orderItemRepository.saveAll(orderItemsList);

    // 6. Clear Cart
    cartRepository.deleteAll(cartItems);

    return convertToOrderDTO(savedOrder, orderItemsList);
}
private OrderResponseDTO convertToOrderDTO(Order order, List<OrderItem> items) {
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setOrderId(order.getId());
        dto.setOrderDate(order.getOrderDate());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus());

        List<OrderItemDTO> itemDTOs = items.stream().map(item -> {
            OrderItemDTO itemDto = new OrderItemDTO();
            itemDto.setProductName(item.getProduct().getName());
            itemDto.setQuantity(item.getQuantity());
            itemDto.setPriceAtPurchase(item.getPriceAtPurchase());
            itemDto.setSubtotal(item.getPriceAtPurchase() * item.getQuantity());
            return itemDto;
        }).toList();

        dto.setItems(itemDTOs);
        return dto;
    }

   public List<OrderResponseDTO> getOrderHistory(String username) {
    User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));

    // Use the new method we just created!
    List<Order> orders = orderRepository.findByUserWithItems(user);

    return orders.stream()
            .map(order -> convertToOrderDTO(order, order.getOrderItems()))
            .toList();
}
}