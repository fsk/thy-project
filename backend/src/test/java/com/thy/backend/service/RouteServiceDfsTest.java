package com.thy.backend.service;

import com.thy.backend.entity.Location;
import com.thy.backend.entity.Transportation;
import com.thy.backend.entity.enums.TransportationType;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RouteServiceDfsTest {

    private static final LocalDate MONDAY_2026_04_06 = LocalDate.of(2026, 4, 6);

    private static final Set<DayOfWeek> OPERATING_MONDAY = Set.of(DayOfWeek.MONDAY);

    private static final Set<DayOfWeek> NO_OPERATING_DAYS = Collections.emptySet();

    private RouteService routeService;

    @BeforeEach
    void setUp() {
        routeService = new RouteService(null, null, null);
    }

    private static Location locationWithId(UUID locationId) {

        return Instancio.of(Location.class)
                .set(field(Location::getId), locationId)
                .create();

    }

    private static Transportation transportation(Location originLocation, Location destinationLocation, TransportationType transportationType, Set<DayOfWeek> operatingDays) {

        return Instancio.of(Transportation.class)
                .set(field(Transportation::getOriginLocation), originLocation)
                .set(field(Transportation::getDestinationLocation), destinationLocation)
                .set(field(Transportation::getTransportationType), transportationType)
                .set(field(Transportation::getOperatingDays), operatingDays)
                .create();
    }

    private static DfsBuffers dfsBuffersWithOriginMarkedVisited(UUID originLocationId) {

        List<List<Transportation>> foundRoutes = new ArrayList<>();
        Set<UUID> visitedLocationIds = new HashSet<>();
        visitedLocationIds.add(originLocationId);
        return new DfsBuffers(foundRoutes, visitedLocationIds);
    }


    private record DfsBuffers(List<List<Transportation>> foundRoutes, Set<UUID> visitedLocationIds) {
    }

    @Test
    void dfs_oneFlight_direct() {

        // Case
        UUID originLocationId = UUID.randomUUID();
        UUID destinationLocationId = UUID.randomUUID();

        Location originLocation = locationWithId(originLocationId);
        Location destinationLocation = locationWithId(destinationLocationId);

        Transportation directFlight = transportation(originLocation, destinationLocation, TransportationType.FLIGHT, OPERATING_MONDAY);

        Map<UUID, List<Transportation>> outgoingEdgesByOriginId = new HashMap<>();
        outgoingEdgesByOriginId.put(originLocationId, new ArrayList<>(List.of(directFlight)));

        DfsBuffers buffers = dfsBuffersWithOriginMarkedVisited(originLocationId);

        // When
        routeService.dfs(originLocationId, destinationLocationId, MONDAY_2026_04_06, outgoingEdgesByOriginId, buffers.foundRoutes(), buffers.visitedLocationIds());

        // Then
        assertEquals(1, buffers.foundRoutes().size());
        assertEquals(1, buffers.foundRoutes().getFirst().size());
    }

    @Test
    void dfs_bus_then_flight() {
        // Case
        UUID originLocationId = UUID.randomUUID();
        UUID hubLocationId = UUID.randomUUID();
        UUID destinationLocationId = UUID.randomUUID();

        Location originLocation = locationWithId(originLocationId);
        Location hubLocation = locationWithId(hubLocationId);
        Location destinationLocation = locationWithId(destinationLocationId);

        Transportation originToHubBus = transportation(originLocation, hubLocation, TransportationType.BUS, OPERATING_MONDAY);

        Transportation hubToDestinationFlight = transportation(hubLocation, destinationLocation, TransportationType.FLIGHT, OPERATING_MONDAY);

        Map<UUID, List<Transportation>> outgoingEdgesByOriginId = new HashMap<>();
        outgoingEdgesByOriginId.put(originLocationId, List.of(originToHubBus));
        outgoingEdgesByOriginId.put(hubLocationId, List.of(hubToDestinationFlight));

        DfsBuffers buffers = dfsBuffersWithOriginMarkedVisited(originLocationId);

        // When
        routeService.dfs(originLocationId, destinationLocationId, MONDAY_2026_04_06, outgoingEdgesByOriginId, buffers.foundRoutes(), buffers.visitedLocationIds());

        // Then
        assertEquals(1, buffers.foundRoutes().size());
        assertEquals(2, buffers.foundRoutes().get(0).size());
    }


    @Test
    void dfs_empty_day() {
        // Case
        UUID originLocationId = UUID.randomUUID();
        UUID destinationLocationId = UUID.randomUUID();

        Location originLocation = locationWithId(originLocationId);
        Location destinationLocation = locationWithId(destinationLocationId);

        Transportation flightWithNoOperatingDays = transportation(originLocation, destinationLocation, TransportationType.FLIGHT, NO_OPERATING_DAYS);

        Map<UUID, List<Transportation>> outgoingEdgesByOriginId = Map.of(originLocationId, List.of(flightWithNoOperatingDays));
        DfsBuffers buffers = dfsBuffersWithOriginMarkedVisited(originLocationId);

        // When
        routeService.dfs(originLocationId, destinationLocationId, MONDAY_2026_04_06, outgoingEdgesByOriginId, buffers.foundRoutes(), buffers.visitedLocationIds());

        // Then
        assertEquals(0, buffers.foundRoutes().size());
    }

    @Test
    void dfs_zero_edge() {
        // Case
        UUID originLocationId = UUID.randomUUID();
        UUID destinationLocationId = UUID.randomUUID();

        Map<UUID, List<Transportation>> outgoingEdgesByOriginId = new HashMap<>();
        DfsBuffers buffers = dfsBuffersWithOriginMarkedVisited(originLocationId);

        // When
        routeService.dfs(originLocationId, destinationLocationId, MONDAY_2026_04_06, outgoingEdgesByOriginId, buffers.foundRoutes(), buffers.visitedLocationIds());

        // Then
        assertEquals(0, buffers.foundRoutes().size());
    }
}
