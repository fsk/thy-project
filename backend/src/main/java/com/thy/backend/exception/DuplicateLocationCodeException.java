package com.thy.backend.exception;

public final class DuplicateLocationCodeException extends IllegalArgumentException {

    public DuplicateLocationCodeException(String locationCode) {
        super("Location code already in use: " + locationCode);
    }
}
