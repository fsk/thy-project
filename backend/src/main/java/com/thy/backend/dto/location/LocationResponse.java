package com.thy.backend.dto.location;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class LocationResponse {

    private final UUID id;
    private final String name;
    private final String country;
    private final String city;
    private final String locationCode;
}
