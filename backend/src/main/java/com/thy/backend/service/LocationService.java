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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;
    private final TransportationRepository transportationRepository;
    private final LocationMapper locationMapper;

    @Transactional
    public LocationResponse create(LocationRequest request) {

        String locationCode = request.getLocationCode();
        boolean isExistByLocationCode = locationRepository.existsByLocationCode(locationCode);

        if (isExistByLocationCode) {
            throw new DuplicateLocationCodeException(locationCode);
        }

        Location entity = locationMapper.toEntity(request);
        return locationMapper.toResponse(locationRepository.save(entity));

    }

    @Transactional(readOnly = true)
    public Page<LocationResponse> findAll(Pageable pageable) {
        return locationRepository.findAll(pageable).map(locationMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public LocationResponse findById(UUID id) {
        return locationMapper.toResponse(getById(id));
    }

    @Transactional
    public LocationResponse update(UUID id, LocationRequest request) {

        String locationCode = request.getLocationCode();
        boolean existsByLocationCodeAndIdNot = locationRepository.existsByLocationCodeAndIdNot(locationCode, id);

        Location entity = getById(id);

        if (existsByLocationCodeAndIdNot) {
            throw new DuplicateLocationCodeException(locationCode);
        }

        locationMapper.updateEntity(request, entity);
        return locationMapper.toResponse(locationRepository.save(entity));
    }

    @Transactional
    public void delete(UUID id) {

        boolean existsByOriginLocation_id = transportationRepository.existsByOriginLocation_Id(id);
        boolean existsByDestinationLocation_id = transportationRepository.existsByDestinationLocation_Id(id);

        getById(id);

        if (existsByOriginLocation_id || existsByDestinationLocation_id) {
            throw new LocationReferencedByTransportationException(id);
        }
        locationRepository.deleteById(id);
    }

    private Location getById(UUID id) {
        return locationRepository.findById(id).orElseThrow(() -> new LocationNotFoundException(id));
    }
}
