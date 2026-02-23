package com.example.ticketing.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * TODO: Complete this entity class.
 *
 * A TicketHold represents a temporary reservation of tickets. When a user
 * requests tickets, we HOLD them for a short window (default: 2 minutes)
 * while they complete payment. If they don't confirm in time, the hold
 * expires and tickets return to the available pool.
 *
 * Your job:
 * 1. Add appropriate Bean Validation constraints on fields
 * 2. Ensure the status enum is stored correctly in the database
 * 3. Add optimistic locking to prevent concurrent modification
 * 4. Auto-populate createdAt on insert
 * 5. Consider what indexes would improve query performance
 */
@Entity
@Table(name = "ticket_holds")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketHold {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_tier_id", nullable = false)
    private TicketTier ticketTier;

    // TODO: Add validation
    @Column(nullable = false, unique = true)
    private String holdToken;

    // TODO: Add validation
    @Column(nullable = false)
    private Integer quantity;

    // TODO: Ensure enum is persisted as a readable string, not an ordinal
    // TODO: Add validation
    @Column(nullable = false)
    private HoldStatus status;

    // TODO: Add validation
    @Column(nullable = false)
    private LocalDateTime expiresAt;

    private LocalDateTime createdAt;

    private LocalDateTime confirmedAt;

    // TODO: Add optimistic locking
    private Long version;

    // TODO: Auto-populate createdAt when the entity is first persisted
}
