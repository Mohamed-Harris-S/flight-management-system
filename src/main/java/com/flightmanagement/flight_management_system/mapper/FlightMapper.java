package com.flightmanagement.flight_management_system.mapper;

import com.flightmanagement.flight_management_system.dto.AirportResponse;
import com.flightmanagement.flight_management_system.dto.FlightResponse;
import com.flightmanagement.flight_management_system.entity.Airport;
import com.flightmanagement.flight_management_system.entity.Flight;
import org.springframework.stereotype.Component;

@Component
public class FlightMapper {

    public AirportResponse toAirportResponse(Airport airport) {
        return new AirportResponse(
                airport.getId(), airport.getCode(), airport.getName(),
                airport.getCity(), airport.getCountry()
        );
    }

    public FlightResponse toFlightResponse(Flight flight) {
        return new FlightResponse(
                flight.getId(), flight.getFlightNumber(),
                toAirportResponse(flight.getSourceAirport()),
                toAirportResponse(flight.getDestinationAirport()),
                flight.getDepartureTime(), flight.getArrivalTime(),
                flight.getTotalSeats(), flight.getAvailableSeats(), flight.getStatus()
        );
    }
}