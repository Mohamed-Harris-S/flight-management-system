package com.flightmanagement.flight_management_system.dto;

import com.flightmanagement.flight_management_system.entity.Booking.BookingStatus;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    private Long id;
    private String bookingReference;
    private FlightResponse flight;
    private String passengerName;
    private Integer passengerAge;
    private BookingStatus status;
    private LocalDateTime bookedAt;


}
