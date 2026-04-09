package com.thy.backend.entity;

import com.thy.backend.entity.enums.TransportationType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity
@Table(name = "transportations")
public class Transportation extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "origin_location_id", nullable = false)
    private Location originLocation;

    @ManyToOne(optional = false)
    @JoinColumn(name = "destination_location_id", nullable = false)
    private Location destinationLocation;

    @Enumerated(EnumType.STRING)
    @Column(name = "transportation_type", nullable = false, length = 30)
    private TransportationType transportationType;

    @ElementCollection
    @CollectionTable(
            name = "transportation_operating_days",
            joinColumns = @JoinColumn(name = "transportation_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "operating_day", nullable = false, length = 20)
    private Set<DayOfWeek> operatingDays = new HashSet<>();

    public boolean isFlight() {
        return transportationType == TransportationType.FLIGHT;
    }

    public boolean isAvailableOn(LocalDate date) {
        if (operatingDays == null || operatingDays.isEmpty()) {
            return false;
        }
        return operatingDays.contains(date.getDayOfWeek());
    }


    public static boolean validationOfTransportationRoute(List<Transportation> connectedTransportationSequence) {

        int n = connectedTransportationSequence.size();

        if (n < 1 || n > 3) {
            return false;
        }

        long flightCount = connectedTransportationSequence.stream().filter(Transportation::isFlight).count();

        if (flightCount != 1) {
            return false;
        }

        int flightIndex = -1;

        for (int i = 0; i < n; i++) {
            if (connectedTransportationSequence.get(i).isFlight()) {
                flightIndex = i;
                break;
            }
        }

        if (flightIndex > 1) {
            return false;
        }

        if (n - flightIndex - 1 > 1) {
            return false;
        }

        for (int i = 0; i < flightIndex; i++) {
            if (connectedTransportationSequence.get(i).isFlight()) {
                return false;
            }
        }

        for (int i = flightIndex + 1; i < n; i++) {
            if (connectedTransportationSequence.get(i).isFlight()) {
                return false;
            }
        }

        return true;
    }


    public static Map<UUID, List<Transportation>> adjacencyMatrix(List<Transportation> transportations) {

        Map<UUID, List<Transportation>> adjacencyMatrix = new HashMap<>();

        for (Transportation t : transportations) {
            UUID from = t.getOriginLocation().getId();
            adjacencyMatrix.computeIfAbsent(from, k -> new ArrayList<>()).add(t);
        }
        return adjacencyMatrix;
    }
}
