package com.flightmanagement.flight_management_system.service;

import com.flightmanagement.flight_management_system.dto.BookingRequest;
import com.flightmanagement.flight_management_system.dto.BookingResponse;
import com.flightmanagement.flight_management_system.entity.Airport;
import com.flightmanagement.flight_management_system.entity.Booking;
import com.flightmanagement.flight_management_system.entity.Flight;
import com.flightmanagement.flight_management_system.entity.User;
import com.flightmanagement.flight_management_system.exception.InvalidRequestException;
import com.flightmanagement.flight_management_system.mapper.BookingMapper;
import com.flightmanagement.flight_management_system.repository.BookingRepository;
import com.flightmanagement.flight_management_system.repository.FlightRepository;
import com.flightmanagement.flight_management_system.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    private  FlightRepository flightRepository;

    @Mock
    private  UserRepository userRepository;

    @Mock
    private  BookingRepository bookingRepository;

    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private BookingService bookingService;



    @Test
    void createBooking_shouldSucceed_whenSeatsAvailable() {

        Flight flight = new Flight(/* ... */);
        flight.setId(1L);
        flight.setAvailableSeats(5);

        User user = new User("Test User", "test@example.com", "hashedpw", User.Role.USER);
        user.setId(1L);

        Airport source = new Airport(/* ... */);
        source.setId(1L);

        Airport destination = new Airport(/* ... */);
        destination.setId(2L);

        flight.setSourceAirport(source);
        flight.setDestinationAirport(destination);

        BookingRequest request = new BookingRequest(1L, "John Doe", 30);
        BookingResponse bookingResponse = new BookingResponse();
        bookingResponse.setBookingReference("BK12345678");

        try (MockedStatic<SecurityContextHolder> mockedContext = mockStatic(SecurityContextHolder.class)) {

            SecurityContext context = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);

            mockedContext.when(SecurityContextHolder::getContext).thenReturn(context);

            when(context.getAuthentication()).thenReturn(authentication);

            when(authentication.getName()).thenReturn("test@example.com");

            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(flightRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(flight));

            Booking savedBooking = new Booking(/* ... */);
            savedBooking.setId(1L);
            savedBooking.setFlight(flight);
            savedBooking.setUser(user);
            savedBooking.setStatus(Booking.BookingStatus.CONFIRMED);
            savedBooking.setBookingReference("BK12345678");

            when(bookingRepository.save(any(Booking.class))).thenReturn(savedBooking);
            when(bookingMapper.toBookingResponse(any(Booking.class)))
                    .thenReturn(bookingResponse);

            BookingResponse response = bookingService.createBooking(request);


            assertNotNull(response);
            assertNotNull(response.getBookingReference());
            assertFalse(response.getBookingReference().isEmpty());
            assertEquals(4, flight.getAvailableSeats());

            verify(flightRepository).findByIdForUpdate(1L);
            verify(flightRepository).save(flight);
            verify(bookingRepository).save(any(Booking.class));
            verify(bookingMapper).toBookingResponse(savedBooking);
        }
    }

    @Test
    void createBooking_shouldThrowInvalidRequest_whenNoSeatsAvailable(){
        Flight flight = new Flight(/* ... */);
        flight.setId(1L);
        flight.setAvailableSeats(0);

        User user = new User("Test User", "test@example.com", "hashedpw", User.Role.USER);
        user.setId(1L);

        BookingRequest request = new BookingRequest(1L, "John Doe", 30);

        try(MockedStatic<SecurityContextHolder> mockedContext = mockStatic(SecurityContextHolder.class)){
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);

            mockedContext.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn("test@example.com");

            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(flightRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(flight));

            assertThrows(InvalidRequestException.class, () -> bookingService.createBooking(request));

        }

    }

    @Test
    void cancelBooking_shouldThrowInvalidRequest_whenAlreadyCancelled(){
        Booking booking = new Booking(/* ... */);
        booking.setId(1L);
        booking.setStatus(Booking.BookingStatus.CANCELLED);

        User user = new User(
                "Test User",
                "test@example.com",
                "hashedpw",
                User.Role.USER
        );
        user.setId(1L);

        booking.setUser(user);
        try(MockedStatic<SecurityContextHolder> mockedContext = mockStatic(SecurityContextHolder.class)){
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);

            mockedContext.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn("test@example.com");

            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

            when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

            assertThrows(InvalidRequestException.class, () -> bookingService.cancelBooking(1L));


            verify(bookingRepository).findById(1L);
            verify(userRepository).findByEmail("test@example.com");
            verifyNoInteractions(flightRepository);
            verify(bookingRepository, never()).save(any());

        }

    }

    @Test
    void cancelBooking_shouldRestoreSeat_whenSuccessful() {

        Flight flight = new Flight(/* ... */);
        flight.setId(1L);
        flight.setAvailableSeats(4);

        Airport source = new Airport(/* ... */);
        source.setId(1L);

        Airport destination = new Airport(/* ... */);
        destination.setId(2L);

        flight.setSourceAirport(source);
        flight.setDestinationAirport(destination);

        User user = new User("Test User", "test@example.com", "hashedpw", User.Role.USER);
        user.setId(1L);

        Booking booking = new Booking(/* ... */);
        booking.setId(1L);
        booking.setUser(user);
        booking.setFlight(flight);
        booking.setStatus(Booking.BookingStatus.CONFIRMED);
        booking.setBookingReference("BK12345678");

        BookingResponse bookingResponse = new BookingResponse();
        bookingResponse.setBookingReference("BK12345678");


        try (MockedStatic<SecurityContextHolder> mockedContext = mockStatic(SecurityContextHolder.class)) {

            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);

            mockedContext.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn("test@example.com");

            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

            when(flightRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(flight));
            when(bookingRepository.save(booking)).thenReturn(booking);

            when(bookingMapper.toBookingResponse(any(Booking.class))).thenReturn(bookingResponse);

            BookingResponse response = bookingService.cancelBooking(1L);

            assertNotNull(response);
            assertEquals( "BK12345678", response.getBookingReference() );
            assertEquals(5, flight.getAvailableSeats());
            assertEquals(Booking.BookingStatus.CANCELLED, booking.getStatus());

            verify(flightRepository).findByIdForUpdate(1L);
            verify(flightRepository).save(flight);
            verify(bookingRepository).save(booking);
            verify(bookingMapper) .toBookingResponse(booking);

        }
    }

}
