package com.thy.backend.repository;

import com.thy.backend.entity.Transportation;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransportationRepository extends JpaRepository<Transportation, UUID> {

    boolean existsByOriginLocation_Id(UUID locationId);

    boolean existsByDestinationLocation_Id(UUID locationId);

    @EntityGraph(attributePaths = {"originLocation", "destinationLocation", "operatingDays"})
    @Override
    List<Transportation> findAll();

    @EntityGraph(attributePaths = {"originLocation", "destinationLocation", "operatingDays"})
    @Override
    Optional<Transportation> findById(UUID id);
}
