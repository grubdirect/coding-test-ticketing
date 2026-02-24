package com.example.ticketing.config;

import com.example.ticketing.entity.*;
import com.example.ticketing.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Seeds the database with ~100,000 ticket holds and ~35,000 orders on startup
 * to create a production-scale dataset for the performance optimization challenge.
 *
 * Skipped during tests via @Profile("!test").
 * Guarded: if holds already exist, seeding is skipped.
 */
@Component
@Profile("!test")
@RequiredArgsConstructor
@Slf4j
public class ReportDataSeeder {

    private final TicketHoldRepository ticketHoldRepository;
    private final EventRepository eventRepository;
    private final TicketTierRepository ticketTierRepository;
    private final JdbcTemplate jdbcTemplate;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void seed() {
        if (ticketHoldRepository.count() > 0) return;

        log.info("Seeding report data...");

        List<Event> events = eventRepository.findAll();
        if (events.isEmpty()) {
            log.warn("No events found -- skipping report data seeding");
            return;
        }

        Random random = new Random(42);
        int holdCount = 0;
        int orderCount = 0;

        // Event 1 gets ~70K holds; Event 2 gets ~30K holds
        int[] holdsPerEvent = {70_000, 30_000};

        for (int e = 0; e < events.size() && e < holdsPerEvent.length; e++) {
            Event event = events.get(e);
            List<TicketTier> tiers = ticketTierRepository.findByEventId(event.getId());
            if (tiers.isEmpty()) continue;

            int targetHolds = holdsPerEvent[e];
            int totalCapacity = tiers.stream().mapToInt(TicketTier::getTotalQuantity).sum();

            List<Object[]> holdRows = new ArrayList<>();
            List<Object[]> orderRows = new ArrayList<>();

            for (TicketTier tier : tiers) {
                int tierHolds = Math.round((float) tier.getTotalQuantity() / totalCapacity * targetHolds);

                for (int i = 0; i < tierHolds; i++) {
                    double roll = random.nextDouble();
                    HoldStatus status;
                    if (roll < 0.15) status = HoldStatus.ACTIVE;
                    else if (roll < 0.50) status = HoldStatus.CONFIRMED;
                    else if (roll < 0.85) status = HoldStatus.EXPIRED;
                    else status = HoldStatus.CANCELLED;

                    int customerIndex = random.nextInt(500) + 1;
                    String customerName = String.format("Customer_%03d", customerIndex);
                    String customerEmail = String.format("customer_%03d@example.com", customerIndex);
                    int quantity = random.nextInt(4) + 1;

                    LocalDateTime createdAt = LocalDateTime.now()
                            .minusDays(random.nextInt(30))
                            .minusHours(random.nextInt(24))
                            .minusMinutes(random.nextInt(60));

                    // status stored as ordinal (default JPA behavior without @Enumerated(STRING))
                    holdRows.add(new Object[]{
                            event.getId(),
                            tier.getId(),
                            UUID.randomUUID().toString(),
                            quantity,
                            status.ordinal(),
                            Timestamp.valueOf(createdAt.plusMinutes(2)),
                            Timestamp.valueOf(createdAt),
                            status == HoldStatus.CONFIRMED ? Timestamp.valueOf(createdAt.plusMinutes(1)) : null
                    });
                    holdCount++;

                    if (status == HoldStatus.CONFIRMED) {
                        BigDecimal totalPrice = tier.getPrice().multiply(BigDecimal.valueOf(quantity));
                        orderRows.add(new Object[]{
                                event.getId(),
                                tier.getId(),
                                "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                                customerName,
                                customerEmail,
                                quantity,
                                totalPrice,
                                Timestamp.valueOf(createdAt.plusMinutes(1))
                        });
                        orderCount++;
                    }
                }
            }

            // Bulk insert holds using JDBC batch (much faster over remote connections)
            log.info("Inserting {} holds for event '{}'...", holdRows.size(), event.getName());
            batchInsertHolds(holdRows);

            log.info("Inserting {} orders for event '{}'...", orderRows.size(), event.getName());
            batchInsertOrders(orderRows);
        }

        log.info("Seeded {} holds and {} orders for report data", holdCount, orderCount);
    }

    private void batchInsertHolds(List<Object[]> rows) {
        String sql = "INSERT INTO ticket_holds (event_id, ticket_tier_id, hold_token, quantity, status, expires_at, created_at, confirmed_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.batchUpdate(sql, rows, 500, (PreparedStatement ps, Object[] row) -> {
            ps.setLong(1, (Long) row[0]);
            ps.setLong(2, (Long) row[1]);
            ps.setString(3, (String) row[2]);
            ps.setInt(4, (int) row[3]);
            ps.setInt(5, (int) row[4]);
            ps.setTimestamp(6, (Timestamp) row[5]);
            ps.setTimestamp(7, (Timestamp) row[6]);
            ps.setTimestamp(8, (Timestamp) row[7]);
        });
    }

    private void batchInsertOrders(List<Object[]> rows) {
        String sql = "INSERT INTO ticket_orders (event_id, ticket_tier_id, order_reference, customer_name, customer_email, quantity, total_price, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.batchUpdate(sql, rows, 500, (PreparedStatement ps, Object[] row) -> {
            ps.setLong(1, (Long) row[0]);
            ps.setLong(2, (Long) row[1]);
            ps.setString(3, (String) row[2]);
            ps.setString(4, (String) row[3]);
            ps.setString(5, (String) row[4]);
            ps.setInt(6, (int) row[5]);
            ps.setBigDecimal(7, (BigDecimal) row[6]);
            ps.setTimestamp(8, (Timestamp) row[7]);
        });
    }
}
