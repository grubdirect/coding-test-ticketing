package com.example.ticketing.repository;

import com.example.ticketing.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Pre-built — DO NOT MODIFY.
 */
@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
}
