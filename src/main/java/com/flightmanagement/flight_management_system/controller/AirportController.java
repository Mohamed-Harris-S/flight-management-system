package com.flightmanagement.flight_management_system.controller;

import com.flightmanagement.flight_management_system.dto.AirportRequest;
import com.flightmanagement.flight_management_system.dto.AirportResponse;
import com.flightmanagement.flight_management_system.service.AirportService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/airports")
public class AirportController {

    private final AirportService airportService;

    public AirportController(AirportService airportService){
        this.airportService = airportService;
    }

    @GetMapping
    public List<AirportResponse> getAllAirports(){
        return airportService.getAllAirports();
    }

    @GetMapping("/{id}")
    public AirportResponse getAirportById(@PathVariable Long id){
        return airportService.getAirportById(id);
    }

    @PostMapping
    public ResponseEntity<AirportResponse> createAirport(@Valid @RequestBody AirportRequest airportRequest){
        AirportResponse response = airportService.createAirport(airportRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public AirportResponse updateAirport(@PathVariable Long id,
                                         @Valid @RequestBody AirportRequest airportRequest){
        return airportService.updateAirport(id, airportRequest);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAirport(@PathVariable Long id){
        airportService.deleteAirport(id);
        return ResponseEntity.noContent().build();
    }

}
