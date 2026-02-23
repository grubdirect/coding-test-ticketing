package com.example.ticketing.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * TODO: Complete this entity class.
 *
 * A TicketOrder is created when a hold is confirmed (payment succeeds).
 * This is the permanent record of a successful ticket purchase.
 *
 * Your job:
 * 1. Add appropriate Bean Validation constraints on fields
 * 2. Add optimistic locking
 * 3. Auto-populate createdAt on insert
 */
@Entity
@Table(name = "ticket_orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketOrder {

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
    private String orderReference;

    // TODO: Add validation
    @Column(nullable = false)
    private String customerName;

    // TODO: Add validation
    @Column(nullable = false)
    private String customerEmail;

    // TODO: Add validation
    @Column(nullable = false)
    private Integer quantity;

    // TODO: Add validation
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    private LocalDateTime createdAt;

    // TODO: Add optimistic locking
    private Long version;

    // TODO: Auto-populate createdAt on insert
}
