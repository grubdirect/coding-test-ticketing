package com.example.ticketing.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DO NOT MODIFY -- this is the expected response structure.
 *
 * Top-level response for the sales report endpoint.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SalesReportResponse {

    private Long eventId;
    private String eventName;
    private String venue;
    private LocalDateTime eventDate;

    private int totalTicketsSold;
    private BigDecimal totalRevenue;

    private List<TierSalesDetail> tierDetails;
    private List<TopCustomer> topCustomers;

    /** Percentage of holds that became CONFIRMED. */
    private double holdToConversionRate;

    /** How long this report took to generate, in milliseconds. */
    private long reportGeneratedInMs;
}
