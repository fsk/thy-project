package com.thy.backend.service;

import com.thy.backend.dto.location.LocationRequest;
import com.thy.backend.dto.location.LocationResponse;
import com.thy.backend.entity.Location;
import com.thy.backend.exception.DuplicateLocationCodeException;
import com.thy.backend.exception.LocationNotFoundException;
import com.thy.backend.exception.LocationReferencedByTransportationException;
import com.thy.backend.mapper.LocationMapper;
import com.thy.backend.repository.LocationRepository;
import com.thy.backend.repository.TransportationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;
    private final TransportationRepository transportationRepository;
    private final LocationMapper locationMapper;

    @Transactional
    public LocationResponse create(LocationRequest request) {
        if (locationRepository.existsByLocationCode(request.getLocationCode())) {
            throw new DuplicateLocationCodeException(request.getLocationCode());
        }
        Location entity = locationMapper.toEntity(request);
        return locationMapper.toResponse(locationRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public List<LocationResponse> findAll() {
        return locationRepository.findAll().stream().map(locationMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public LocationResponse findById(UUID id) {
        return locationMapper.toResponse(getById(id));
    }

    @Transactional
    public LocationResponse update(UUID id, LocationRequest request) {
        Location entity = getById(id);
        if (locationRepository.existsByLocationCodeAndIdNot(request.getLocationCode(), id)) {
            throw new DuplicateLocationCodeException(request.getLocationCode());
        }
        locationMapper.updateEntity(request, entity);
        return locationMapper.toResponse(locationRepository.save(entity));
    }

    @Transactional
    public void delete(UUID id) {
        getById(id);
        if (transportationRepository.existsByOriginLocation_Id(id) || transportationRepository.existsByDestinationLocation_Id(id)) {
            throw new LocationReferencedByTransportationException(id);
        }
        locationRepository.deleteById(id);
    }

    private Location getById(UUID id) {
        return locationRepository.findById(id).orElseThrow(() -> new LocationNotFoundException(id));
    }
}
