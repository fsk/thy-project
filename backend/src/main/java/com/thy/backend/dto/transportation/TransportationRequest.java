package com.thy.backend.dto.transportation;

import com.thy.backend.entity.enums.TransportationType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.DayOfWeek;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransportationRequest {

    @NotNull
    private UUID originLocationId;

    @NotNull
    private UUID destinationLocationId;

    @NotNull
    private TransportationType transportationType;

    @NotEmpty
    private Set<DayOfWeek> operatingDays;
}
