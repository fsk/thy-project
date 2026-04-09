package com.thy.backend.controller;

import com.thy.backend.api.ApiResponse;
import com.thy.backend.dto.location.LocationRequest;
import com.thy.backend.dto.location.LocationResponse;
import com.thy.backend.service.LocationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @PostMapping
    public ResponseEntity<ApiResponse<LocationResponse>> create(@Valid @RequestBody LocationRequest request) {
        LocationResponse body = locationService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<LocationResponse>builder()
                        .success(true)
                        .data(body)
                        .resultMessage("Location created successfully")
                        .build());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<LocationResponse>>> list(@PageableDefault(size = 20, sort = "locationCode", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<LocationResponse> page = locationService.findAll(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<Page<LocationResponse>>builder()
                        .success(true)
                        .data(page)
                        .resultMessage("Locations listed successfully")
                        .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LocationResponse>> get(@PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<LocationResponse>builder()
                        .success(true)
                        .data(locationService.findById(id))
                        .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<LocationResponse>> update(@PathVariable UUID id, @Valid @RequestBody LocationRequest request) {

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<LocationResponse>builder()
                        .success(true)
                        .data(locationService.update(id, request))
                        .resultMessage("Location updated successfully")
                        .build());

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteById(@PathVariable UUID id) {

        locationService.delete(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.<Void>builder()
                        .success(true)
                        .resultMessage("Location deleted successfully")
                        .build());

    }
}
