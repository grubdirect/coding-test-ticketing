package com.example.ticketing.service;

import com.example.ticketing.dto.*;
import com.example.ticketing.notification.NotificationService;
import com.example.ticketing.repository.EventRepository;
import com.example.ticketing.repository.TicketHoldRepository;
import com.example.ticketing.repository.TicketOrderRepository;
import com.example.ticketing.repository.TicketTierRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * TODO: Implement ALL methods in this service.
 *
 * This service handles the full ticket lifecycle: hold → confirm → cancel/expire.
 *
 * Key business rules:
 * - Tickets can only be held after the event's onsaleTime
 * - A hold reserves tickets for a limited time (configured in application.yml)
 * - Confirming a hold creates a permanent order
 * - Cancelling or expiring a hold releases tickets back to the pool
 * - The system must NEVER oversell — even under extreme concurrent load
 *
 * Available exceptions (pre-built):
 * - EventNotFoundException, NotOnSaleException, SoldOutException, HoldExpiredException
 *
 * The HoldExpiryScheduler (pre-built) calls releaseExpiredHolds() every 30 seconds.
 */
@Service
public class TicketingService {

    private final EventRepository eventRepository;
    private final TicketTierRepository ticketTierRepository;
    private final TicketHoldRepository ticketHoldRepository;
    private final TicketOrderRepository ticketOrderRepository;
    private final NotificationService notificationService;

    @Value("${ticketing.hold-duration-seconds:120}")
    private int holdDurationSeconds;

    public TicketingService(EventRepository eventRepository,
                            TicketTierRepository ticketTierRepository,
                            TicketHoldRepository ticketHoldRepository,
                            TicketOrderRepository ticketOrderRepository,
                            NotificationService notificationService) {
        this.eventRepository = eventRepository;
        this.ticketTierRepository = ticketTierRepository;
        this.ticketHoldRepository = ticketHoldRepository;
        this.ticketOrderRepository = ticketOrderRepository;
        this.notificationService = notificationService;
    }

    /**
     * TODO: Hold tickets for a given event and tier.
     *
     * Must handle 10,000+ concurrent requests without overselling.
     */
    public HoldResponse holdTickets(Long eventId, HoldTicketRequest request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * TODO: Confirm a held ticket — convert to a permanent order.
     *
     * Only active, non-expired holds can be confirmed.
     * After successfully creating the order, send a confirmation email
     * to the customer using the NotificationService.
     */
    public OrderResponse confirmHold(String holdToken, ConfirmHoldRequest request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * TODO: Cancel a hold — release tickets back to the available pool.
     */
    public void cancelHold(String holdToken) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * TODO: Release all expired holds.
     *
     * Called by the scheduler every 30 seconds. Must be safe to run
     * concurrently with user-facing operations (confirm, cancel).
     *
     * @return the number of holds released
     */
    public int releaseExpiredHolds() {
        return 0;
    }

    /**
     * TODO: Get availability for all tiers of an event.
     *
     * Each tier should show a status: "AVAILABLE", "LOW_STOCK" (< 10% remaining), or "SOLD_OUT".
     * The overall event saleStatus should be "NOT_ON_SALE", "ON_SALE", or "SOLD_OUT".
     */
    public EventAvailabilityResponse getEventAvailability(Long eventId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * TODO: Get all confirmed orders for an event.
     */
    public List<OrderResponse> getEventOrders(Long eventId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
