package com.thy.backend.controller;

import com.thy.backend.api.ApiResponse;
import com.thy.backend.dto.transportation.TransportationRequest;
import com.thy.backend.dto.transportation.TransportationResponse;
import com.thy.backend.service.TransportationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/transportations")
@RequiredArgsConstructor
public class TransportationController {

    private final TransportationService transportationService;

    @PostMapping
    public ResponseEntity<ApiResponse<TransportationResponse>> create(@Valid @RequestBody TransportationRequest request) {
        TransportationResponse body = transportationService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<TransportationResponse>builder()
                        .success(true)
                        .data(body)
                        .resultMessage("Transportation created successfully")
                        .build()
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TransportationResponse>>> list() {
        return ResponseEntity.ok(
                ApiResponse.<List<TransportationResponse>>builder()
                        .success(true)
                        .data(transportationService.findAll())
                        .resultMessage("Transportations listed successfully")
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TransportationResponse>> get(@PathVariable UUID id) {
        return ResponseEntity.ok(
                ApiResponse.<TransportationResponse>builder()
                        .success(true)
                        .data(transportationService.findById(id))
                        .build()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TransportationResponse>> update(@PathVariable UUID id, @Valid @RequestBody TransportationRequest request) {
        return ResponseEntity.ok(
                ApiResponse.<TransportationResponse>builder()
                        .success(true)
                        .data(transportationService.update(id, request))
                        .resultMessage("Transportation updated successfully")
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        transportationService.delete(id);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .resultMessage("Transportation deleted successfully")
                        .build()
        );
    }
}
