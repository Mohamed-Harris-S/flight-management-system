package com.flightmanagement.flight_management_system.repository;

import com.flightmanagement.flight_management_system.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FlightRepository extends JpaRepository<Flight,Long> {

}
