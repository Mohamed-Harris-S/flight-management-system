package com.flightmanagement.flight_management_system.service;

import com.flightmanagement.flight_management_system.dto.AirportResponse;
import com.flightmanagement.flight_management_system.dto.FlightRequest;
import com.flightmanagement.flight_management_system.dto.FlightResponse;
import com.flightmanagement.flight_management_system.entity.Airport;
import com.flightmanagement.flight_management_system.entity.Flight;
import com.flightmanagement.flight_management_system.exception.InvalidRequestException;
import com.flightmanagement.flight_management_system.exception.ResourceNotFoundException;
import com.flightmanagement.flight_management_system.repository.AirportRepository;
import com.flightmanagement.flight_management_system.repository.FlightRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FlightService {
    private final FlightRepository flightRepository;
    private final AirportRepository airportRepository;

    public FlightService(FlightRepository flightRepository, AirportRepository airportRepository){
        this.flightRepository = flightRepository;
        this.airportRepository = airportRepository;
    }

    public FlightResponse createFlight(FlightRequest request){

        if(request.getSourceAirportId().equals(request.getDestinationAirportId())){
            throw new InvalidRequestException("Source and destination airport cannot be the same");
        }

        if(!request.getArrivalTime().isAfter(request.getDepartureTime())){
            throw new InvalidRequestException("Arrival time must be after departure time");
        }

        Airport source = airportRepository.findById(request.getSourceAirportId())
                .orElseThrow(() -> new ResourceNotFoundException("Source airport not found with id: "
                + request.getSourceAirportId()));

        Airport destination = airportRepository.findById(request.getDestinationAirportId())
                .orElseThrow(() -> new ResourceNotFoundException("Destination airport not found with id: "
                        + request.getDestinationAirportId()));

        Flight flight = new Flight();

        flight.setFlightNumber(request.getFlightNumber());
        flight.setSourceAirport(source);
        flight.setDestinationAirport(destination);
        flight.setDepartureTime(request.getDepartureTime());
        flight.setArrivalTime(request.getArrivalTime());
        flight.setTotalSeats(request.getTotalSeats());

        flight.setAvailableSeats(request.getTotalSeats());
        flight.setStatus("SCHEDULED");

        Flight savedFlight = flightRepository.save(flight);

        return toResponse(savedFlight);

    }

    public List<FlightResponse> getAllFlights(){
        return flightRepository.findAll().stream().map(this::toResponse).toList();
    }

    public FlightResponse getFlightById(Long id){

        Flight flight = flightRepository.findById(id)
                .orElseThrow( () -> new ResourceNotFoundException("Flight not found with id:" + id) );

        return toResponse(flight);
    }



    private FlightResponse toResponse(Flight flight) {

        AirportResponse sourceResponse = toAirportResponse(flight.getSourceAirport());

        AirportResponse destinationResponse = toAirportResponse(flight.getDestinationAirport());

        return new FlightResponse(
                flight.getId(),
                flight.getFlightNumber(),
                sourceResponse,
                destinationResponse,
                flight.getDepartureTime(),
                flight.getArrivalTime(),
                flight.getTotalSeats(),
                flight.getAvailableSeats(),
                flight.getStatus()
        );
    }

    private AirportResponse toAirportResponse(Airport airport) {
        return new AirportResponse(
                airport.getId(),
                airport.getCode(),
                airport.getName(),
                airport.getCity(),
                airport.getCountry()
        );
    }
}

