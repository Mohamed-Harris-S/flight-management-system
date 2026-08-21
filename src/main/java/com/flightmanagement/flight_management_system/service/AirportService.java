package com.flightmanagement.flight_management_system.service;

import com.flightmanagement.flight_management_system.dto.AirportRequest;
import com.flightmanagement.flight_management_system.dto.AirportResponse;
import com.flightmanagement.flight_management_system.entity.Airport;
import com.flightmanagement.flight_management_system.repository.AirportRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AirportService {

    private final AirportRepository airportRepository;

    public AirportService(AirportRepository airportRepository){
        this.airportRepository = airportRepository;
    }

    public AirportResponse createAirport(AirportRequest airportRequest){
        if(airportRepository.existsByCode(airportRequest.getCode())){
            throw new RuntimeException("Airport already exists");
        }
        Airport airport = new Airport();
        airport.setName(airportRequest.getName());
        airport.setCity(airportRequest.getCity());
        airport.setCode(airportRequest.getCode());
        airport.setCountry(airportRequest.getCountry());

        Airport savedAirport = airportRepository.save(airport);
        return toResponse(savedAirport);
    }

    public AirportResponse getAirportById(Long id){
        Airport airport = airportRepository.findById(id).
                orElseThrow( () -> new RuntimeException("Airport not found with id: "+id) );
        return toResponse(airport);
    }

    public List<AirportResponse> getAllAirports() {
        return airportRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public AirportResponse updateAirport(Long id, AirportRequest request) {

        Airport airport = airportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Airport not found with id: " + id
                ));

        airport.setName(request.getName());
        airport.setCity(request.getCity());
        airport.setCountry(request.getCountry());

        Airport savedAirport = airportRepository.save(airport);

        return toResponse(savedAirport);
    }

    public void deleteAirport(Long id) {
        if (!airportRepository.existsById(id)) {
            throw new RuntimeException(
                    "Airport not found with id: " + id
            );
        }
        airportRepository.deleteById(id);
    }

    private AirportResponse toResponse(Airport airport) {
        AirportResponse response = new AirportResponse();

        response.setId(airport.getId());
        response.setCode(airport.getCode());
        response.setName(airport.getName());
        response.setCity(airport.getCity());
        response.setCountry(airport.getCountry());

        return response;
    }



}
