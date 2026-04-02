package com.thy.backend.repository;

import com.thy.backend.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LocationRepository extends JpaRepository<Location, UUID> {

    boolean existsByLocationCode(String locationCode);

    boolean existsByLocationCodeAndIdNot(String locationCode, UUID id);

    Optional<Location> findByLocationCode(String locationCode);
}
