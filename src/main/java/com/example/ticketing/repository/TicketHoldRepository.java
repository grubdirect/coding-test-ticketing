package com.example.ticketing.repository;

import com.example.ticketing.entity.TicketHold;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * TODO: Add query methods for hold management.
 *
 * You will need methods to:
 * - Look up a hold by its token
 * - Find expired active holds (for the scheduler cleanup)
 * - Count active holds for a given tier (for availability reporting)
 */
@Repository
public interface TicketHoldRepository extends JpaRepository<TicketHold, Long> {

    // TODO: Add query methods
}
