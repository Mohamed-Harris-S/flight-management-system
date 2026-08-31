package com.flightmanagement.flight_management_system.repository;

import com.flightmanagement.flight_management_system.entity.Flight;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface FlightRepository extends JpaRepository<Flight,Long> {
    @Query("SELECT f FROM Flight f WHERE " +
            "(:sourceCode IS NULL OR f.sourceAirport.code = :sourceCode) AND " +
            "(:destCode IS NULL OR f.destinationAirport.code = :destCode) AND " +
            "(:date IS NULL OR FUNCTION('DATE', f.departureTime) = :date)")
    List<Flight> searchFlights(
            @Param("sourceCode") String sourceCode,
            @Param("destCode") String destCode,
            @Param("date") LocalDate date
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT f FROM Flight f WHERE f.id = :id")
    Optional<Flight> findByIdForUpdate(@Param("id") Long id);
}
