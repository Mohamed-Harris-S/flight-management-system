package com.flightmanagement.flight_management_system.service;

import com.flightmanagement.flight_management_system.dto.AirportResponse;
import com.flightmanagement.flight_management_system.dto.BookingRequest;
import com.flightmanagement.flight_management_system.dto.BookingResponse;
import com.flightmanagement.flight_management_system.dto.FlightResponse;
import com.flightmanagement.flight_management_system.entity.Airport;
import com.flightmanagement.flight_management_system.entity.Booking;
import com.flightmanagement.flight_management_system.entity.Booking.BookingStatus;
import com.flightmanagement.flight_management_system.entity.Flight;
import com.flightmanagement.flight_management_system.entity.User;
import com.flightmanagement.flight_management_system.exception.InvalidRequestException;
import com.flightmanagement.flight_management_system.exception.ResourceNotFoundException;
import com.flightmanagement.flight_management_system.repository.BookingRepository;
import com.flightmanagement.flight_management_system.repository.FlightRepository;
import com.flightmanagement.flight_management_system.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class BookingService {

    private final FlightRepository flightRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    public BookingService(FlightRepository flightRepository,
                          UserRepository userRepository, BookingRepository bookingRepository){
        this.flightRepository = flightRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
    }

    @Transactional
    public BookingResponse createBooking(BookingRequest bookingRequest){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new ResourceNotFoundException("User not found: " + email));

        Flight flight = flightRepository.findByIdForUpdate(bookingRequest.getFlightId())
                .orElseThrow(() ->  new ResourceNotFoundException(
                "Flight not found: "+bookingRequest.getFlightId()));

        if(flight.getAvailableSeats()<=0){
            throw new InvalidRequestException( "No seats available on this flight");
        }

        flight.setAvailableSeats(flight.getAvailableSeats()-1);
        flightRepository.save(flight);



        Booking booking = new Booking();

        booking.setBookingReference(generateBookingReference());
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setFlight(flight);
        booking.setUser(user);
        booking.setPassengerName(bookingRequest.getPassengerName());
        booking.setPassengerAge(bookingRequest.getPassengerAge());

        Booking savedBooking = bookingRepository.save(booking);
        return toResponse(savedBooking);


    }
    private String generateBookingReference() {
        return "BK" +
                UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
                        .toUpperCase();
    }


    private BookingResponse toResponse(Booking booking) {

        Flight flight = booking.getFlight();

        AirportResponse sourceResponse = toAirportResponse(flight.getSourceAirport());

        AirportResponse destinationResponse = toAirportResponse(flight.getDestinationAirport());

        FlightResponse flightResponse = new FlightResponse(
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


        return new BookingResponse(
                booking.getId(),
                booking.getBookingReference(),
                flightResponse,
                booking.getPassengerName(),
                booking.getPassengerAge(),
                booking.getStatus(),
                booking.getBookedAt()

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
