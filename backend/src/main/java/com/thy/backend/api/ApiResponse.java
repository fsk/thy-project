package com.thy.backend.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.sql.Timestamp;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private boolean success;
    private T data;
    private String resultMessage;
    private String errorMessage;

    @Builder.Default
    private Timestamp timestamp = new Timestamp(System.currentTimeMillis());
}
