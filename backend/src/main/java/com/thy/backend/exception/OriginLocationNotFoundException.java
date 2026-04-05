package com.thy.backend.exception;

import java.util.UUID;

public final class OriginLocationNotFoundException extends ResourceNotFoundException {

    public OriginLocationNotFoundException(UUID locationId) {
        super("Origin location not found: " + locationId);
    }
}
