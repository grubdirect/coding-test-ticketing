package com.example.ticketing.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * DO NOT MODIFY -- this is the expected response structure.
 *
 * Per-tier breakdown within the sales report.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TierSalesDetail {

    private Long tierId;
    private String tierName;
    private BigDecimal price;

    private int ticketsSold;
    private BigDecimal revenue;

    private long activeHolds;
    private long confirmedHolds;
    private long expiredHolds;
    private long cancelledHolds;

    private List<RecentOrderSummary> recentOrders;
}
