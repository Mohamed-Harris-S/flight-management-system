package com.flightmanagement.flight_management_system.service;

import com.flightmanagement.flight_management_system.dto.FlightRequest;
import com.flightmanagement.flight_management_system.dto.FlightResponse;
import com.flightmanagement.flight_management_system.entity.Airport;
import com.flightmanagement.flight_management_system.entity.Flight;
import com.flightmanagement.flight_management_system.exception.InvalidRequestException;
import com.flightmanagement.flight_management_system.exception.ResourceNotFoundException;
import com.flightmanagement.flight_management_system.repository.AirportRepository;
import com.flightmanagement.flight_management_system.repository.FlightRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FlightServiceTest {

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private AirportRepository airportRepository;

    @InjectMocks
    private FlightService flightService;

    @Test
    void createFlight_shouldSucceed_whenRequestIsValid(){
        Airport source = new Airport("JFK", "JFK Airport", "New York", "USA");
        source.setId(1L);
        Airport destination = new Airport("LAX", "LAX Airport", "Los Angeles", "USA");
        destination.setId(2L);

        FlightRequest request = new FlightRequest(
                "AA100",
                1L,
                2L,
                LocalDateTime.of(2026, 9, 10, 9, 30),
                LocalDateTime.of(2026, 9, 10, 12, 15),
                180
        );

        when(airportRepository.findById(1L)).thenReturn(Optional.of(source));
        when(airportRepository.findById(2L)).thenReturn(Optional.of(destination));
        when(flightRepository.save(any(Flight.class))).thenAnswer(
                invocation -> {
                    Flight f = invocation.getArgument(0);
                    f.setId(1L);
                    return f;
                }
        );

        FlightResponse response = flightService.createFlight(request);

        assertEquals(180, response.getAvailableSeats());
        assertEquals(Flight.FlightStatus.SCHEDULED, response.getStatus());

    }


    @Test
    void createFlight_shouldThrowInvalidRequest_whenSourceEqualsDestination(){
        FlightRequest request = new FlightRequest(
                "AA100",
                1L,
                1L,
                LocalDateTime.of(2026, 9, 10, 9, 30),
                LocalDateTime.of(2026, 9, 10, 12, 15),
                180
        );

        assertThrows(InvalidRequestException.class, () -> flightService.createFlight(request));

        verifyNoInteractions(airportRepository);

    }

    @Test
    void createFlight_shouldThrowInvalidRequest_whenArrivalBeforeDeparture(){
        FlightRequest request = new FlightRequest(
                "AA100",
                1L,
                2L,
                LocalDateTime.of(2026, 9, 10, 12, 30),
                LocalDateTime.of(2026, 9, 10, 9, 15),
                180
        );

        assertThrows(InvalidRequestException.class, () -> flightService.createFlight(request));

        verifyNoInteractions(airportRepository);
    }

    @Test
    void createFlight_shouldThrowNotFound_whenDestinationAirportMissing(){
        Airport source = new Airport("JFK", "JFK Airport", "New York", "USA");
        source.setId(1L);

        FlightRequest request = new FlightRequest(
                "AA100",
                1L,
                2L,
                LocalDateTime.of(2026, 9, 10, 9, 30),
                LocalDateTime.of(2026, 9, 10, 12, 15),
                180
        );

        when(airportRepository.findById(1L)).thenReturn(Optional.of(source));
        when(airportRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, ()-> flightService.createFlight(request));

        verify(airportRepository).findById(1L);
        verify(airportRepository).findById(2L);
        verifyNoInteractions(flightRepository);
    }


}
