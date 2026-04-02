package com.thy.backend.exception;

import java.util.UUID;

public final class LocationReferencedByTransportationException extends IllegalStateException {

    public LocationReferencedByTransportationException(UUID locationId) {
        super("Location " + locationId + " is referenced by transportation records; remove or reassign them first.");
    }
}
