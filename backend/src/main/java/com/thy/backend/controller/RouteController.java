package com.thy.backend.controller;

import com.thy.backend.api.ApiResponse;
import com.thy.backend.dto.route.RouteResponse;
import com.thy.backend.service.RouteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/routes")
@RequiredArgsConstructor
public class RouteController {

    private final RouteService routeService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<RouteResponse>>> search(@RequestParam UUID originLocationId, @RequestParam UUID destinationLocationId,@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<RouteResponse> data = routeService.findValidRoutes(originLocationId, destinationLocationId, date);
        return ResponseEntity.ok(
                ApiResponse.<List<RouteResponse>>builder()
                        .success(true)
                        .data(data)
                        .resultMessage("Routes computed successfully")
                        .build()
        );
    }
}
