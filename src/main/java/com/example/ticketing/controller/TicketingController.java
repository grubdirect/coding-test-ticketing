package com.example.ticketing.controller;

import com.example.ticketing.service.TicketingService;
import org.springframework.web.bind.annotation.*;

/**
 * TODO: Implement all REST endpoints.
 *
 * Refer to the API Reference tab in INSTRUCTIONS.html for the exact
 * endpoint paths, HTTP methods, request/response bodies, and status codes.
 *
 * Requirements:
 * - Validate request bodies
 * - Use appropriate HTTP status codes for each operation
 * - Delegate to TicketingService — keep the controller thin
 */
@RestController
@RequestMapping("/api")
public class TicketingController {

    private final TicketingService ticketingService;

    public TicketingController(TicketingService ticketingService) {
        this.ticketingService = ticketingService;
    }

    // TODO: Implement 5 endpoints (see INSTRUCTIONS.html → API Reference)
}
