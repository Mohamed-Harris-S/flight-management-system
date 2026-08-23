package com.flightmanagement.flight_management_system.dto;


import com.flightmanagement.flight_management_system.entity.Flight.FlightStatus;

import java.time.LocalDateTime;

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

    public FlightResponse() {
    }

    public FlightResponse(Long id, String flightNumber, AirportResponse sourceAirport,
                          AirportResponse destinationAirport, LocalDateTime departureTime, LocalDateTime arrivalTime,
                          Integer totalSeats, Integer availableSeats, FlightStatus status) {
        this.id = id;
        this.flightNumber = flightNumber;
        this.sourceAirport = sourceAirport;
        this.destinationAirport = destinationAirport;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.totalSeats = totalSeats;
        this.availableSeats = availableSeats;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public AirportResponse getSourceAirport() {
        return sourceAirport;
    }

    public void setSourceAirport(AirportResponse sourceAirport) {
        this.sourceAirport = sourceAirport;
    }

    public AirportResponse getDestinationAirport() {
        return destinationAirport;
    }

    public void setDestinationAirport(AirportResponse destinationAirport) {
        this.destinationAirport = destinationAirport;
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

    public Integer getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(Integer availableSeats) {
        this.availableSeats = availableSeats;
    }

    public FlightStatus getStatus() {
        return status;
    }

    public void setStatus(FlightStatus status) {
        this.status = status;
    }
}
