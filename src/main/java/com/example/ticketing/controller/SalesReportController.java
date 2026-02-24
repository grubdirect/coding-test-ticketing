package com.example.ticketing.controller;

import com.example.ticketing.dto.SalesReportResponse;
import com.example.ticketing.service.SalesReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * DO NOT MODIFY -- this controller is complete.
 *
 * Exposes the sales report endpoint for the performance optimization challenge.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SalesReportController {

    private final SalesReportService salesReportService;

    @GetMapping("/events/{eventId}/sales-report")
    public ResponseEntity<SalesReportResponse> getSalesReport(@PathVariable Long eventId) {
        return ResponseEntity.ok(salesReportService.generateReport(eventId));
    }
}
