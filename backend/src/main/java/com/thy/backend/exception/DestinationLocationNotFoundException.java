package com.thy.backend.exception;

import java.util.UUID;

public final class DestinationLocationNotFoundException extends ResourceNotFoundException {

    public DestinationLocationNotFoundException(UUID locationId) {
        super("Destination location not found: " + locationId);
    }
}
