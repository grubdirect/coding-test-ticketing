package com.example.ticketing.service;

import com.example.ticketing.notification.LoggingNotificationService;
import com.example.ticketing.repository.EventRepository;
import com.example.ticketing.repository.TicketHoldRepository;
import com.example.ticketing.repository.TicketOrderRepository;
import com.example.ticketing.repository.TicketTierRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * TODO: Write integration tests for the TicketingService.
 *
 * Hints:
 * - Use @SpringBootTest with @ActiveProfiles("test") for H2 database
 * - The DataInitializer seeds 2 events (see DataInitializer.java for details)
 * - Event 1 (Taylor Swift) is already on sale; Event 2 (Champions League) is not yet
 * - Hold duration in test profile is 5 seconds
 */
@SpringBootTest
@ActiveProfiles("test")
class TicketingServiceTest {

    @Autowired
    private TicketingService ticketingService;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private TicketTierRepository ticketTierRepository;

    @Autowired
    private TicketHoldRepository ticketHoldRepository;

    @Autowired
    private TicketOrderRepository ticketOrderRepository;

    @Autowired
    private LoggingNotificationService notificationService;

    @BeforeEach
    void setUp() {
        ticketOrderRepository.deleteAll();
        ticketHoldRepository.deleteAll();
        notificationService.clear();
        // Reset available quantities to original values
        ticketTierRepository.findAll().forEach(tier -> {
            tier.setAvailableQuantity(tier.getTotalQuantity());
            ticketTierRepository.save(tier);
        });
    }

    @Nested
    @DisplayName("Hold Tickets")
    class HoldTickets {

        @Test
        @DisplayName("should hold tickets and decrement available quantity")
        void shouldHoldTickets() {
            // TODO: Implement
        }

        @Test
        @DisplayName("should throw SoldOutException when no tickets available")
        void shouldThrowSoldOutWhenNoTickets() {
            // TODO: Implement
        }

        @Test
        @DisplayName("should throw NotOnSaleException before onsale time")
        void shouldRejectHoldBeforeOnsale() {
            // TODO: Implement
        }
    }

    @Nested
    @DisplayName("Confirm Hold")
    class ConfirmHold {

        @Test
        @DisplayName("should confirm hold and create order with correct total price")
        void shouldConfirmHoldAndCreateOrder() {
            // TODO: Implement
        }

        @Test
        @DisplayName("should reject confirmation of expired hold")
        void shouldRejectExpiredHold() {
            // TODO: Implement
        }
    }

    @Nested
    @DisplayName("Cancel Hold")
    class CancelHold {

        @Test
        @DisplayName("should cancel hold and release tickets back to pool")
        void shouldCancelAndReleaseTickets() {
            // TODO: Implement
        }
    }

    @Nested
    @DisplayName("Expired Hold Cleanup")
    class ExpiredHoldCleanup {

        @Test
        @DisplayName("should release expired holds and return tickets to pool")
        void shouldReleaseExpiredHolds() {
            // TODO: Implement
        }
    }

    @Nested
    @DisplayName("Availability")
    class Availability {

        @Test
        @DisplayName("should return correct availability after holds")
        void shouldReturnCorrectAvailability() {
            // TODO: Implement
        }
    }

    @Nested
    @DisplayName("Email Notification")
    class EmailNotification {

        @Test
        @DisplayName("should send confirmation email when hold is confirmed")
        void shouldSendEmailOnConfirm() {
            // TODO: Implement
            // Hold tickets, confirm the hold, then verify that
            // notificationService.getSentMessages() contains the expected email
        }

        @Test
        @DisplayName("should not send email when hold confirmation fails (expired)")
        void shouldNotSendEmailOnFailedConfirm() {
            // TODO: Implement
            // Hold tickets, wait for expiry, attempt confirm, verify no email sent
        }
    }

    // ────────────────────────────────────────────────────────────────────────
    //  ★  REQUIRED: Concurrent Oversell Prevention
    //
    //  Write a multi-threaded integration test that simulates the "hot onsale"
    //  scenario. This test must PROVE that your system never oversells tickets
    //  even when many users attempt to buy simultaneously.
    //
    //  Scenario:
    //    - A tier has a small number of tickets remaining
    //    - Many more users than tickets try to hold at the exact same instant
    //    - Assert: only the correct number succeed, the rest are rejected
    //    - Assert: available quantity is never negative
    //
    //  This is the most important test in the entire exercise.
    // ────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("★ Concurrent Oversell Prevention (REQUIRED)")
    class ConcurrentOversellPrevention {

        @Test
        @DisplayName("many concurrent users fight for limited tickets — no overselling")
        void shouldPreventOversellUnderConcurrentLoad() throws InterruptedException {
            // TODO: Implement this test — it is REQUIRED, not a bonus.
        }
    }
}
