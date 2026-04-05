package com.thy.backend.mapper;

import com.thy.backend.dto.location.LocationRequest;
import com.thy.backend.dto.location.LocationResponse;
import com.thy.backend.entity.Location;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface LocationMapper {

    LocationResponse toResponse(Location location);

    @Mapping(target = "id", ignore = true)
    Location toEntity(LocationRequest request);

    @Mapping(target = "id", ignore = true)
    void updateEntity(LocationRequest request, @MappingTarget Location location);
}
