package com.example.ticketing.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DO NOT MODIFY -- this is the expected response structure.
 *
 * Lightweight summary of a recent order within a tier.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecentOrderSummary {

    private String orderReference;
    private String customerName;
    private int quantity;
    private BigDecimal totalPrice;
    private LocalDateTime createdAt;
}
