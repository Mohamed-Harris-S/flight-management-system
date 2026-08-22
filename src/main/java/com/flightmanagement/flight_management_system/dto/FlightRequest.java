package com.flightmanagement.flight_management_system.dto;


import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public class FlightRequest {

    @NotBlank
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

    public FlightRequest() {
    }

    public FlightRequest(String flightNumber, Long sourceAirportId, Long destinationAirportId,
                         LocalDateTime departureTime, LocalDateTime arrivalTime, Integer totalSeats) {
        this.flightNumber = flightNumber;
        this.sourceAirportId = sourceAirportId;
        this.destinationAirportId = destinationAirportId;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.totalSeats = totalSeats;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public Long getSourceAirportId() {
        return sourceAirportId;
    }

    public void setSourceAirportId(Long sourceAirportId) {
        this.sourceAirportId = sourceAirportId;
    }

    public Long getDestinationAirportId() {
        return destinationAirportId;
    }

    public void setDestinationAirportId(Long destinationAirportId) {
        this.destinationAirportId = destinationAirportId;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public Integer getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(Integer totalSeats) {
        this.totalSeats = totalSeats;
    }
}
