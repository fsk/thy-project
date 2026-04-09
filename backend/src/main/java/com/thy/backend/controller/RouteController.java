package com.thy.backend.controller;

import com.thy.backend.api.ApiResponse;
import com.thy.backend.dto.route.RouteResponse;
import com.thy.backend.service.RouteService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.HttpStatus;
import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/routes")
@RequiredArgsConstructor
@Validated
public class RouteController {

    private final RouteService routeService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<RouteResponse>>> search(
            @RequestParam UUID originLocationId,
            @RequestParam UUID destinationLocationId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        Page<RouteResponse> data = routeService.findValidRoutes(originLocationId, destinationLocationId, date, page, size);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<Page<RouteResponse>>builder()
                        .success(true)
                        .data(data)
                        .resultMessage("Routes computed successfully")
                        .build()
        );
    }
}
