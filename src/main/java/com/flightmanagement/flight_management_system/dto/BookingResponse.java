package com.flightmanagement.flight_management_system.dto;

import com.flightmanagement.flight_management_system.entity.Booking.BookingStatus;

import java.time.LocalDateTime;

public class BookingResponse {
    private Long id;
    private String bookingReference;
    private FlightResponse flight;
    private String passengerName;
    private Integer passengerAge;
    private BookingStatus status;
    private LocalDateTime bookedAt;

    public BookingResponse() {
    }

    public BookingResponse(Long id, String bookingReference, FlightResponse flight,
                           String passengerName,Integer passengerAge, BookingStatus status,
                           LocalDateTime bookedAt) {
        this.id = id;
        this.bookingReference = bookingReference;
        this.flight = flight;
        this.passengerName = passengerName;
        this.passengerAge = passengerAge;
        this.status = status;
        this.bookedAt = bookedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBookingReference() {
        return bookingReference;
    }

    public void setBookingReference(String bookingReference) {
        this.bookingReference = bookingReference;
    }

    public FlightResponse getFlight() {
        return flight;
    }

    public void setFlight(FlightResponse flight) {
        this.flight = flight;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    public Integer getPassengerAge() {
        return passengerAge;
    }

    public void setPassengerAge(Integer passengerAge) {
        this.passengerAge = passengerAge;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public LocalDateTime getBookedAt() {
        return bookedAt;
    }

    public void setBookedAt(LocalDateTime bookedAt) {
        this.bookedAt = bookedAt;
    }
}
