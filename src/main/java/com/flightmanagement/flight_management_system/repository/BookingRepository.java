package com.flightmanagement.flight_management_system.repository;

import com.flightmanagement.flight_management_system.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking,Long> {

    List<Booking> findByUserId(Long userId);

}
