package com.flightmanagement.flight_management_system.service;

import com.flightmanagement.flight_management_system.dto.AirportResponse;
import com.flightmanagement.flight_management_system.dto.FlightRequest;
import com.flightmanagement.flight_management_system.dto.FlightResponse;
import com.flightmanagement.flight_management_system.dto.FlightStatusUpdateRequest;
import com.flightmanagement.flight_management_system.entity.Airport;
import com.flightmanagement.flight_management_system.entity.Flight;
import com.flightmanagement.flight_management_system.exception.InvalidRequestException;
import com.flightmanagement.flight_management_system.exception.ResourceNotFoundException;
import com.flightmanagement.flight_management_system.mapper.FlightMapper;
import com.flightmanagement.flight_management_system.repository.AirportRepository;
import com.flightmanagement.flight_management_system.repository.FlightRepository;
import com.flightmanagement.flight_management_system.entity.Flight.FlightStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class FlightService {
    private final FlightRepository flightRepository;
    private final AirportRepository airportRepository;
    private final FlightMapper flightMapper;

    public FlightService(FlightRepository flightRepository, AirportRepository airportRepository, FlightMapper flightMapper){
        this.flightRepository = flightRepository;
        this.airportRepository = airportRepository;
        this.flightMapper = flightMapper;
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
        flight.setStatus(FlightStatus.SCHEDULED);

        Flight savedFlight = flightRepository.save(flight);

        return flightMapper.toFlightResponse(savedFlight);

    }

    public Page<FlightResponse> getAllFlights(Pageable pageable){
        return flightRepository.findAll(pageable).map(flightMapper::toFlightResponse);
    }

    public FlightResponse getFlightById(Long id){

        Flight flight = flightRepository.findById(id)
                .orElseThrow( () -> new ResourceNotFoundException("Flight not found with id:" + id) );

        return flightMapper.toFlightResponse(flight);
    }

    public FlightResponse updateFlightStatus(Long id, FlightStatusUpdateRequest request){
        Flight flight = flightRepository.findById(id)
                .orElseThrow( () -> new ResourceNotFoundException("Flight not found with id:" + id) );

        flight.setStatus(request.getStatus());
        Flight savedFlight = flightRepository.save(flight);
        return flightMapper.toFlightResponse(savedFlight);
    }

    public Page<FlightResponse> searchFlights(String source, String destination, LocalDate date,Pageable pageable){
        return flightRepository.searchFlights(source,destination,date,pageable).map(flightMapper::toFlightResponse);
    }


}

