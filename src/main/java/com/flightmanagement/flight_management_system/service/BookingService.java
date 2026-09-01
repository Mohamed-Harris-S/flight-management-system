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
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
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

        User user = getCurrentUser();

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

    @Transactional
    public BookingResponse cancelBooking(Long id){
        Booking booking = bookingRepository.findById(id)
                .orElseThrow( ()-> new ResourceNotFoundException("Booking not found: " + id));

        checkOwnershipOrAdmin(booking.getUser().getId());

        if(booking.getStatus() == BookingStatus.CANCELLED){
            throw new InvalidRequestException(
                    "Booking is already cancelled"
            );
        }

        Flight flight = flightRepository.findByIdForUpdate(booking.getFlight().getId())
                .orElseThrow( ()-> new ResourceNotFoundException("Flight not found"));

        flight.setAvailableSeats(flight.getAvailableSeats()+1);

        booking.setStatus(BookingStatus.CANCELLED);

        flightRepository.save(flight);

        Booking savedBooking = bookingRepository.save(booking);

        return toResponse(savedBooking);

    }

    public BookingResponse getBookingById(Long id){
        Booking booking = bookingRepository.findById(id)
                .orElseThrow( () -> new ResourceNotFoundException(
                        "Booking not found: " + id
                ));

        checkOwnershipOrAdmin(booking.getUser().getId());

        return toResponse(booking);
    }

    public List<BookingResponse> getUserBookings(Long userId){

        checkOwnershipOrAdmin(userId);

        return bookingRepository.findByUserId(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private String generateBookingReference() {
        return "BK" +
                UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
                        .toUpperCase();
    }

    private User getCurrentUser(){
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private boolean isAdmin(){
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority()
                                .equals("ROLE_ADMIN"));
    }

    private void checkOwnershipOrAdmin(Long ownerId){
        User currentUser = getCurrentUser();
        boolean isOwner = currentUser.getId().equals(ownerId);

        if(!isOwner && !isAdmin()){
            throw new AccessDeniedException("You do not have permission to access this resource");
        }

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
