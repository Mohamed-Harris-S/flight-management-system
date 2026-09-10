package com.flightmanagement.flight_management_system.dto;


import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FlightRequest {

    @NotBlank
    @Size(max = 20)
    private String flightNumber;

    @NotNull
    private Long sourceAirportId;

    @NotNull
    private Long destinationAirportId;

    @NotNull
    private LocalDateTime departureTime;

    @NotNull
    private LocalDateTime arrivalTime;

    @NotNull
    @Positive
    @Max(853)
    private Integer totalSeats;


}
