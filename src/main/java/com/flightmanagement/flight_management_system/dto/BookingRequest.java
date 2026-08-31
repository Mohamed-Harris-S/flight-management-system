package com.flightmanagement.flight_management_system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class BookingRequest {

    @NotNull
    private Long flightId;

    @NotBlank
    private String passengerName;

    @NotNull
    @Positive
    private Integer passengerAge;

    public BookingRequest(){

    }

    public BookingRequest(Long flightId, String passengerName, Integer passengerAge) {
        this.flightId = flightId;
        this.passengerName = passengerName;
        this.passengerAge = passengerAge;
    }

    public Long getFlightId() {
        return flightId;
    }

    public void setFlightId(Long flightId) {
        this.flightId = flightId;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    public Integer getPassengerAge() {
        return passengerAge;
    }

    public void setPassengerAge(Integer passengerAge) {
        this.passengerAge = passengerAge;
    }
}
