package com.ecommerce.platform.dto;

import lombok.Data;

@Data
public class OrderItemDTO {
    private String productName;
    private int quantity;
    private Double priceAtPurchase;
    private Double subtotal;
}