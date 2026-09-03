package com.flightmanagement.flight_management_system.service;

import com.flightmanagement.flight_management_system.dto.AirportRequest;
import com.flightmanagement.flight_management_system.dto.AirportResponse;
import com.flightmanagement.flight_management_system.entity.Airport;
import com.flightmanagement.flight_management_system.exception.DuplicateResourceException;
import com.flightmanagement.flight_management_system.exception.ResourceNotFoundException;
import com.flightmanagement.flight_management_system.repository.AirportRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class AirportServiceTest {

    @Mock
    private AirportRepository airportRepository;

    @InjectMocks
    private  AirportService airportService;

    @Test
    void createAirport_shouldSucceed_whenCodeIsUnique(){

        //Arrange
        AirportRequest request = new AirportRequest("MAA", "Chennai International Airport", "Chennai", "India");
        when(airportRepository.existsByCode("MAA")).thenReturn(false);
        when(airportRepository.save(any(Airport.class))).thenAnswer(invocation -> {
            Airport a = invocation.getArgument(0);
            a.setId(1L);
            return a;
        });

        //Act
        AirportResponse response = airportService.createAirport(request);

        //Assert
        assertEquals("MAA",response.getCode());
        assertEquals(1L,response.getId());
        verify(airportRepository).save(any(Airport.class));

    }

    @Test
    void createAirport_shouldThrowDuplicateException_whenCodeExists(){
        AirportRequest request = new AirportRequest("JFK", "JFK Airport", "New York", "USA");
        when(airportRepository.existsByCode("JFK")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> airportService.createAirport(request));

        verify(airportRepository, never()).save(any());

    }

    @Test
    void getAirportById_shouldThrowNotFound_whenIdDoesNotExist(){

        //Arrange
        Long id = 1L;
        when(airportRepository.findById(id)).thenReturn(Optional.empty());

        //Act + Assert
        assertThrows(ResourceNotFoundException.class, ()-> airportService.getAirportById(id));

    }


}
