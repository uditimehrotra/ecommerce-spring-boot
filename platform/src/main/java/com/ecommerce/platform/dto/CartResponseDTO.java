package com.ecommerce.platform.dto;

import lombok.Data;
import java.util.List;

@Data
public class CartResponseDTO {
    private List<CartItemDTO> items;
    private Double grandTotal;
}