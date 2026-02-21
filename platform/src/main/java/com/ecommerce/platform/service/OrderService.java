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
        throw new RuntimeException("Cannot checkout an empty cart");
    }

    // --- STEP 1: STOCK VERIFICATION ---
    for (CartItem item : cartItems) {
        Product product = item.getProduct();
        if (product.getStockQuantity() < item.getQuantity()) {
            throw new RuntimeException("Insufficient stock for product: " + product.getName() + 
                                       ". Available: " + product.getStockQuantity());
        }
    }

    // --- STEP 2: CREATE ORDER ---
    Order order = new Order();
    order.setUser(user);
    order.setOrderDate(LocalDateTime.now());
    order.setStatus("COMPLETED");

    double total = cartItems.stream()
            .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
            .sum();
    order.setTotalAmount(total);
    Order savedOrder = orderRepository.save(order);

    // --- STEP 3: CONVERT ITEMS & SUBTRACT STOCK ---
    List<OrderItem> orderItems = new ArrayList<>();
    for (CartItem cartItem : cartItems) {
        Product product = cartItem.getProduct();
        
        // Subtract stock and save product update
        product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
        productRepository.save(product); 

        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(savedOrder);
        orderItem.setProduct(product);
        orderItem.setQuantity(cartItem.getQuantity());
        orderItem.setPriceAtPurchase(product.getPrice());
        
        orderItems.add(orderItem);
    }

    // Save the order items explicitly
    orderItemRepository.saveAll(orderItems);

    // --- STEP 4: CLEAR THE CART (CRITICAL) ---
    cartRepository.deleteAll(cartItems); 

    // --- STEP 5: RETURN CLEAN RECEIPT ---
    return convertToOrderDTO(savedOrder, orderItems);
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
}