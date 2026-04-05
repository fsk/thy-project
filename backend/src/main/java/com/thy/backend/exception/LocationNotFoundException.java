package com.thy.backend.exception;

import java.util.UUID;

public final class LocationNotFoundException extends ResourceNotFoundException {

    public LocationNotFoundException(UUID id) {
        super("Location not found: " + id);
    }
}
