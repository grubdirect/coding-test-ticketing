package com.example.ticketing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TODO: Add Bean Validation annotations on the fields.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HoldTicketRequest {

    private Long tierId;

    private Integer quantity;
}
