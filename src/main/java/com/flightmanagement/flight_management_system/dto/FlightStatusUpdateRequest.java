package com.flightmanagement.flight_management_system.dto;

import com.flightmanagement.flight_management_system.entity.Flight.FlightStatus;
import jakarta.validation.constraints.NotNull;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FlightStatusUpdateRequest {

    @NotNull
    private FlightStatus status;

}



