package com.thy.backend.service;

import com.thy.backend.dto.transportation.TransportationRequest;
import com.thy.backend.dto.transportation.TransportationResponse;
import com.thy.backend.entity.Location;
import com.thy.backend.entity.Transportation;
import com.thy.backend.exception.DestinationLocationNotFoundException;
import com.thy.backend.exception.OriginLocationNotFoundException;
import com.thy.backend.exception.TransportationNotFoundException;
import com.thy.backend.mapper.TransportationMapper;
import com.thy.backend.repository.LocationRepository;
import com.thy.backend.repository.TransportationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransportationService {

    private final TransportationRepository transportationRepository;
    private final LocationRepository locationRepository;
    private final TransportationMapper transportationMapper;

    @Transactional
    public TransportationResponse create(TransportationRequest request) {
        Location origin = locationRepository.findById(request.getOriginLocationId()).orElseThrow(() -> new OriginLocationNotFoundException(request.getOriginLocationId()));
        Location destination = locationRepository.findById(request.getDestinationLocationId()).orElseThrow(() -> new DestinationLocationNotFoundException(request.getDestinationLocationId()));

        Transportation entity = new Transportation();
        transportationMapper.updateFromRequest(request, entity);
        entity.setOriginLocation(origin);
        entity.setDestinationLocation(destination);
        return transportationMapper.toResponse(transportationRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public List<TransportationResponse> findAll() {
        return transportationRepository.findAll().stream().map(transportationMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public TransportationResponse findById(UUID id) {
        return transportationMapper.toResponse(getById(id));
    }

    @Transactional
    public TransportationResponse update(UUID id, TransportationRequest request) {
        Transportation entity = getById(id);

        Location origin = locationRepository.findById(request.getOriginLocationId()).orElseThrow(() -> new OriginLocationNotFoundException(request.getOriginLocationId()));
        Location destination = locationRepository.findById(request.getDestinationLocationId()).orElseThrow(() -> new DestinationLocationNotFoundException(request.getDestinationLocationId()));

        transportationMapper.updateFromRequest(request, entity);
        entity.setOriginLocation(origin);
        entity.setDestinationLocation(destination);
        return transportationMapper.toResponse(transportationRepository.save(entity));
    }

    @Transactional
    public void delete(UUID id) {
        if (!transportationRepository.existsById(id)) {
            throw new TransportationNotFoundException(id);
        }
        transportationRepository.deleteById(id);
    }

    private Transportation getById(UUID id) {
        return transportationRepository.findById(id).orElseThrow(() -> new TransportationNotFoundException(id));
    }
}
