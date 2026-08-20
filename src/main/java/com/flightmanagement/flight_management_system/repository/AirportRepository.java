package com.flightmanagement.flight_management_system.repository;

import com.flightmanagement.flight_management_system.entity.Airport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AirportRepository extends JpaRepository<Airport,Long> {
    boolean existsByCode(String code);

    Optional<Airport> findByCode(String code);

}
