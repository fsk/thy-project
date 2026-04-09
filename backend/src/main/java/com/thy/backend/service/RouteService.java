package com.thy.backend.service;

import com.thy.backend.dto.route.RouteResponse;
import com.thy.backend.dto.transportation.TransportationResponse;
import com.thy.backend.entity.Transportation;
import com.thy.backend.exception.LocationNotFoundException;
import com.thy.backend.mapper.TransportationMapper;
import com.thy.backend.repository.LocationRepository;
import com.thy.backend.repository.TransportationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    public Page<RouteResponse> findValidRoutes(UUID originLocationId, UUID destinationLocationId, LocalDate date, int page, int size) {
        List<RouteResponse> all = computeValidRoutes(originLocationId, destinationLocationId, date);
        int safeSize = Math.min(Math.max(size, 1), 100);
        long total = all.size();
        int totalPages = total == 0 ? 0 : (int) Math.ceil(total / (double) safeSize);
        int safePage = Math.max(page, 0);
        if (totalPages > 0 && safePage >= totalPages) {
            safePage = totalPages - 1;
        }
        int from = safePage * safeSize;
        List<RouteResponse> content = from >= total ? List.of() : all.subList(from, (int) Math.min(from + safeSize, total));
        Pageable pageable = PageRequest.of(safePage, safeSize);
        return new PageImpl<>(content, pageable, total);
    }

    private List<RouteResponse> computeValidRoutes(UUID originLocationId, UUID destinationLocationId, LocalDate date) {
        if (originLocationId.equals(destinationLocationId)) {
            return List.of();
        }

        boolean existsByOriginalLocationId = locationRepository.existsById(originLocationId);

        if (!existsByOriginalLocationId) {
            throw new LocationNotFoundException(originLocationId);
        }

        boolean existsByDestinationLocationId = locationRepository.existsById(destinationLocationId);

        if (!existsByDestinationLocationId) {
            throw new LocationNotFoundException(destinationLocationId);
        }

        Map<UUID, List<Transportation>> outgoing = Transportation.adjacencyMatrix(transportationRepository.findAll());

        List<List<Transportation>> paths = new ArrayList<>();
        Set<UUID> visitedLocations = new HashSet<>();
        visitedLocations.add(originLocationId);
        dfs(originLocationId, destinationLocationId, date, outgoing, paths, visitedLocations);

        return paths.stream().map(this::toRouteResponse).toList();
    }


    public void dfs(UUID from, UUID to, LocalDate date, Map<UUID, List<Transportation>> outgoing, List<List<Transportation>> results, Set<UUID> visitedLocations) {
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
