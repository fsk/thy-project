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
import java.util.HashSet;
import java.util.Set;

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
}
