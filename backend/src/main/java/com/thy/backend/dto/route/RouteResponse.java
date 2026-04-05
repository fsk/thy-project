package com.thy.backend.dto.route;

import com.thy.backend.dto.transportation.TransportationResponse;

import java.util.List;

public record RouteResponse(List<TransportationResponse> legs) {
}
