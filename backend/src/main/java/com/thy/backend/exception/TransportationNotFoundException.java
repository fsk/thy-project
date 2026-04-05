package com.thy.backend.exception;

import java.util.UUID;

public final class TransportationNotFoundException extends ResourceNotFoundException {

    public TransportationNotFoundException(UUID id) {
        super("Transportation not found: " + id);
    }
}
