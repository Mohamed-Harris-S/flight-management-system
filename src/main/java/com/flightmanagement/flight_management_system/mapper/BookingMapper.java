package com.flightmanagement.flight_management_system.mapper;

import com.flightmanagement.flight_management_system.dto.BookingResponse;
import com.flightmanagement.flight_management_system.dto.FlightResponse;
import com.flightmanagement.flight_management_system.entity.Booking;
import org.springframework.stereotype.Component;

@Component
public class BookingMapper {

    private final FlightMapper flightMapper;

    public BookingMapper(FlightMapper flightMapper){
        this.flightMapper = flightMapper;
    }

    public BookingResponse toBookingResponse(Booking booking) {

        FlightResponse flightResponse =
                flightMapper.toFlightResponse(booking.getFlight());


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


}
