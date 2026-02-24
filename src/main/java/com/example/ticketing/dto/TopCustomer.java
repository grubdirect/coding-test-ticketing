package com.example.ticketing.dto;

import lombok.*;

import java.math.BigDecimal;

/**
 * DO NOT MODIFY -- this is the expected response structure.
 *
 * Aggregated customer data showing top spenders for an event.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TopCustomer {

    private String customerName;
    private String customerEmail;
    private int totalTickets;
    private BigDecimal totalSpending;
    private int orderCount;
}
