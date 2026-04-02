package com.thy.backend.mapper;

import com.thy.backend.dto.transportation.TransportationRequest;
import com.thy.backend.dto.transportation.TransportationResponse;
import com.thy.backend.entity.Transportation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.time.DayOfWeek;
import java.util.HashSet;
import java.util.Set;

@Mapper(componentModel = "spring", uses = LocationMapper.class)
public interface TransportationMapper {

    @Mapping(source = "originLocation", target = "origin")
    @Mapping(source = "destinationLocation", target = "destination")
    @Mapping(target = "operatingDays", source = "operatingDays", qualifiedByName = "copyOperatingDaysSet")
    TransportationResponse toResponse(Transportation entity);

    @Named("copyOperatingDaysSet")
    default Set<DayOfWeek> copyOperatingDaysSet(Set<DayOfWeek> value) {
        return value == null ? null : new HashSet<>(value);
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "originLocation", ignore = true)
    @Mapping(target = "destinationLocation", ignore = true)
    @Mapping(target = "operatingDays", source = "operatingDays", qualifiedByName = "copyOperatingDaysSet")
    void updateFromRequest(TransportationRequest request, @MappingTarget Transportation entity);
}
