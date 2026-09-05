package com.flightmanagement.flight_management_system.controller;

import com.flightmanagement.flight_management_system.dto.FlightRequest;
import com.flightmanagement.flight_management_system.dto.FlightResponse;
import com.flightmanagement.flight_management_system.dto.FlightStatusUpdateRequest;
import com.flightmanagement.flight_management_system.service.FlightService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/flights")
public class FlightController {
    private final FlightService flightService;

    public FlightController(FlightService flightService){
        this.flightService = flightService;
    }

    @GetMapping
    public Page<FlightResponse> getAllFlights(Pageable pageable){
        return flightService.getAllFlights(pageable);
    }

    @GetMapping("/{id}")
    public FlightResponse getFlightById(@PathVariable Long id){
        return flightService.getFlightById(id);
    }

    @PostMapping
    public ResponseEntity<FlightResponse> createFlight(@Valid @RequestBody FlightRequest request){
        FlightResponse response = flightService.createFlight(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}")
    public FlightResponse updateFlightStatus(@PathVariable Long id,
                                             @Valid @RequestBody FlightStatusUpdateRequest request){
        return flightService.updateFlightStatus(id,request);
    }

    @GetMapping("/search")
    public Page<FlightResponse> searchFlights(
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String destination,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date,
            Pageable pageable
            ){
        return flightService.searchFlights(source,destination,date,pageable);
    }


}
