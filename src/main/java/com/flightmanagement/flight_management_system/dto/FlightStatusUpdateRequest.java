package com.flightmanagement.flight_management_system.dto;

import com.flightmanagement.flight_management_system.entity.Flight.FlightStatus;
import jakarta.validation.constraints.NotNull;



public class FlightStatusUpdateRequest {

    @NotNull
    private FlightStatus status;

    public FlightStatusUpdateRequest() {
    }

    public FlightStatusUpdateRequest(FlightStatus status) {
        this.status = status;
    }

    public FlightStatus getStatus() {
        return status;
    }

    public void setStatus(FlightStatus status) {
        this.status = status;
    }
}



