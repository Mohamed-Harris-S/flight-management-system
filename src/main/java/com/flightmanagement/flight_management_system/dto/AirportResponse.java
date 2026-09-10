package com.flightmanagement.flight_management_system.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AirportResponse {
    private Long id;
    private String code;
    private String name;
    private String city;
    private String country;

}
