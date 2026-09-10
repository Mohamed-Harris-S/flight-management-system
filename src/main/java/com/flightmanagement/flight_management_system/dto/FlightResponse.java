package com.flightmanagement.flight_management_system.dto;


import com.flightmanagement.flight_management_system.entity.Flight.FlightStatus;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FlightResponse {

    private Long id;
    private String flightNumber;
    private AirportResponse sourceAirport;
    private AirportResponse destinationAirport;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private Integer totalSeats;
    private Integer availableSeats;
    private FlightStatus status;


}
