package com.thy.backend.service;

import com.thy.backend.dto.route.RouteResponse;
import com.thy.backend.dto.transportation.TransportationResponse;
import com.thy.backend.entity.Transportation;
import com.thy.backend.exception.LocationNotFoundException;
import com.thy.backend.mapper.TransportationMapper;
import com.thy.backend.repository.LocationRepository;
import com.thy.backend.repository.TransportationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RouteService {

    private final TransportationRepository transportationRepository;
    private final LocationRepository locationRepository;
    private final TransportationMapper transportationMapper;

    @Transactional(readOnly = true)
    public List<RouteResponse> findValidRoutes(UUID originLocationId, UUID destinationLocationId, LocalDate date) {
        if (originLocationId.equals(destinationLocationId)) {
            return List.of();
        }
        if (!locationRepository.existsById(originLocationId)) {
            throw new LocationNotFoundException(originLocationId);
        }
        if (!locationRepository.existsById(destinationLocationId)) {
            throw new LocationNotFoundException(destinationLocationId);
        }

        Map<UUID, List<Transportation>> outgoing = Transportation.adjacencyMatris(transportationRepository.findAll());
        List<List<Transportation>> paths = new ArrayList<>();
        Set<UUID> visitedLocations = new HashSet<>();
        visitedLocations.add(originLocationId);
        dfs(originLocationId, destinationLocationId, date, outgoing, paths, visitedLocations);

        return paths.stream().map(this::toRouteResponse).toList();
    }


    private static void dfs(UUID from, UUID to, LocalDate date, Map<UUID, List<Transportation>> outgoing, List<List<Transportation>> results, Set<UUID> visitedLocations) {
        Deque<DfsFrame> stack = new ArrayDeque<>();
        stack.push(new DfsFrame(from, 0));
        List<Transportation> path = new ArrayList<>();

        while (!stack.isEmpty()) {
            DfsFrame frame = stack.peek();
            List<Transportation> edges = outgoing.getOrDefault(frame.at(), List.of());

            if (frame.nextEdgeIndex() >= edges.size()) {
                stack.pop();
                if (!path.isEmpty()) {
                    Transportation back = path.removeLast();
                    visitedLocations.remove(back.getDestinationLocation().getId());
                }
                continue;
            }

            int idx = frame.nextEdgeIndex();
            stack.pop();
            stack.push(new DfsFrame(frame.at(), idx + 1));
            Transportation edge = edges.get(idx);
            if (!edge.isAvailableOn(date)) {
                continue;
            }
            UUID next = edge.getDestinationLocation().getId();
            if (!next.equals(to) && visitedLocations.contains(next)) {
                continue;
            }

            path.add(edge);
            visitedLocations.add(next);

            if (next.equals(to)) {
                if (Transportation.validationOfTransportationRoute(path)) {
                    results.add(new ArrayList<>(path));
                }
                path.removeLast();
                visitedLocations.remove(next);
                continue;
            }

            if (path.size() >= 3) {
                path.removeLast();
                visitedLocations.remove(next);
                continue;
            }

            stack.push(new DfsFrame(next, 0));
        }
    }

    private record DfsFrame(UUID at, int nextEdgeIndex) {
    }

    private RouteResponse toRouteResponse(List<Transportation> transportations) {
        List<TransportationResponse> responses = transportations.stream().map(transportationMapper::toResponse).toList();
        return new RouteResponse(responses);
    }
}
