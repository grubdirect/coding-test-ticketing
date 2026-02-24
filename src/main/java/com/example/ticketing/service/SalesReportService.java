package com.example.ticketing.service;

import com.example.ticketing.dto.*;
import com.example.ticketing.entity.*;
import com.example.ticketing.exception.EventNotFoundException;
import com.example.ticketing.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ============================================================
 *  PERFORMANCE OPTIMIZATION CHALLENGE
 * ============================================================
 *
 * This service generates a sales report for a given event.
 * It is CORRECT — it returns the right data — but it is
 * extremely slow (~10 seconds on the seeded dataset).
 *
 * YOUR TASK: optimize this endpoint so that reportGeneratedInMs
 * is consistently under 100 ms.
 *
 * RULES:
 *   - The response DTO structure must NOT change
 *   - The endpoint URL must NOT change
 *   - The returned data must remain correct
 *   - You MAY add repository methods, indexes, and queries
 *   - You MAY modify this service and the entity classes
 *   - Measure your progress with the reportGeneratedInMs field
 *
 * TARGET: reportGeneratedInMs < 100
 * ============================================================
 */
@Service
@RequiredArgsConstructor
public class SalesReportService {

    private final EventRepository eventRepository;
    private final TicketHoldRepository ticketHoldRepository;
    private final TicketOrderRepository ticketOrderRepository;

    public SalesReportResponse generateReport(Long eventId) {
        long start = System.currentTimeMillis();

        // ---- Fetch the event ----
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found: " + eventId));

        List<TicketTier> tiers = event.getTiers();

        List<TierSalesDetail> tierDetails = new ArrayList<>();
        int totalTicketsSold = 0;
        BigDecimal totalRevenue = BigDecimal.ZERO;

        for (TicketTier tier : tiers) {

            // ---- Hold counts per tier ----
            List<TicketHold> allHolds = ticketHoldRepository.findAll();

            List<TicketHold> tierHolds = allHolds.stream()
                    .filter(h -> h.getEvent().getName().equals(event.getName())
                            && h.getTicketTier().getTierName().equals(tier.getTierName()))
                    .collect(Collectors.toList());

            long activeCount = ticketHoldRepository.findAll().stream()
                    .filter(h -> h.getEvent().getName().equals(event.getName())
                            && h.getTicketTier().getTierName().equals(tier.getTierName())
                            && h.getStatus() == HoldStatus.ACTIVE)
                    .count();

            long confirmedCount = ticketHoldRepository.findAll().stream()
                    .filter(h -> h.getEvent().getName().equals(event.getName())
                            && h.getTicketTier().getTierName().equals(tier.getTierName())
                            && h.getStatus() == HoldStatus.CONFIRMED)
                    .count();

            long expiredCount = ticketHoldRepository.findAll().stream()
                    .filter(h -> h.getEvent().getName().equals(event.getName())
                            && h.getTicketTier().getTierName().equals(tier.getTierName())
                            && h.getStatus() == HoldStatus.EXPIRED)
                    .count();

            long cancelledCount = ticketHoldRepository.findAll().stream()
                    .filter(h -> h.getEvent().getName().equals(event.getName())
                            && h.getTicketTier().getTierName().equals(tier.getTierName())
                            && h.getStatus() == HoldStatus.CANCELLED)
                    .count();

            // ---- Orders per tier ----
            List<TicketOrder> allOrders = ticketOrderRepository.findAll();

            List<TicketOrder> tierOrders = allOrders.stream()
                    .filter(o -> o.getEvent().getName().equals(event.getName())
                            && o.getTicketTier().getTierName().equals(tier.getTierName()))
                    .collect(Collectors.toList());

            int sold = tierOrders.stream()
                    .mapToInt(TicketOrder::getQuantity)
                    .sum();

            BigDecimal revenue = tierOrders.stream()
                    .map(TicketOrder::getTotalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            List<RecentOrderSummary> recentOrders = tierOrders.stream()
                    .sorted(Comparator.comparing(TicketOrder::getCreatedAt).reversed())
                    .limit(5)
                    .map(o -> RecentOrderSummary.builder()
                            .orderReference(o.getOrderReference())
                            .customerName(o.getCustomerName())
                            .quantity(o.getQuantity())
                            .totalPrice(o.getTotalPrice())
                            .createdAt(o.getCreatedAt())
                            .build())
                    .collect(Collectors.toList());

            tierDetails.add(TierSalesDetail.builder()
                    .tierId(tier.getId())
                    .tierName(tier.getTierName())
                    .price(tier.getPrice())
                    .ticketsSold(sold)
                    .revenue(revenue)
                    .activeHolds(activeCount)
                    .confirmedHolds(confirmedCount)
                    .expiredHolds(expiredCount)
                    .cancelledHolds(cancelledCount)
                    .recentOrders(recentOrders)
                    .build());

            totalTicketsSold += sold;
            totalRevenue = totalRevenue.add(revenue);
        }

        // ---- Top 10 customers by total spending ----
        Event eventAgain = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found: " + eventId));

        List<TicketOrder> allOrdersForCustomers = ticketOrderRepository.findAll();

        List<TicketOrder> eventOrders = allOrdersForCustomers.stream()
                .filter(o -> o.getEvent().getName().equals(eventAgain.getName()))
                .collect(Collectors.toList());

        Map<String, List<TicketOrder>> ordersByCustomer = eventOrders.stream()
                .collect(Collectors.groupingBy(TicketOrder::getCustomerEmail));

        List<TopCustomer> topCustomers = ordersByCustomer.entrySet().stream()
                .map(entry -> {
                    List<TicketOrder> customerOrders = entry.getValue();
                    return TopCustomer.builder()
                            .customerName(customerOrders.get(0).getCustomerName())
                            .customerEmail(entry.getKey())
                            .totalTickets(customerOrders.stream()
                                    .mapToInt(TicketOrder::getQuantity).sum())
                            .totalSpending(customerOrders.stream()
                                    .map(TicketOrder::getTotalPrice)
                                    .reduce(BigDecimal.ZERO, BigDecimal::add))
                            .orderCount(customerOrders.size())
                            .build();
                })
                .sorted(Comparator.comparing(TopCustomer::getTotalSpending).reversed())
                .limit(10)
                .collect(Collectors.toList());

        // ---- Hold-to-conversion rate ----
        long totalHolds = ticketHoldRepository.findAll().stream()
                .filter(h -> h.getEvent().getName().equals(event.getName()))
                .count();

        long confirmedHolds = ticketHoldRepository.findAll().stream()
                .filter(h -> h.getEvent().getName().equals(event.getName())
                        && h.getStatus() == HoldStatus.CONFIRMED)
                .count();

        double conversionRate = totalHolds > 0
                ? (double) confirmedHolds / totalHolds * 100.0
                : 0.0;

        long elapsed = System.currentTimeMillis() - start;

        return SalesReportResponse.builder()
                .eventId(eventAgain.getId())
                .eventName(eventAgain.getName())
                .venue(eventAgain.getVenue())
                .eventDate(eventAgain.getEventDate())
                .totalTicketsSold(totalTicketsSold)
                .totalRevenue(totalRevenue)
                .tierDetails(tierDetails)
                .topCustomers(topCustomers)
                .holdToConversionRate(Math.round(conversionRate * 100.0) / 100.0)
                .reportGeneratedInMs(elapsed)
                .build();
    }
}
