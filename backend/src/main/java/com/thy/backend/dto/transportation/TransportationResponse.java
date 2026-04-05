package com.thy.backend.dto.transportation;

import com.thy.backend.dto.location.LocationResponse;
import com.thy.backend.entity.enums.TransportationType;
import lombok.Builder;
import lombok.Getter;

import java.time.DayOfWeek;
import java.util.Set;
import java.util.UUID;

@Getter
@Builder
public class TransportationResponse {

    private final UUID id;
    private final LocationResponse origin;
    private final LocationResponse destination;
    private final TransportationType transportationType;
    private final Set<DayOfWeek> operatingDays;
}
