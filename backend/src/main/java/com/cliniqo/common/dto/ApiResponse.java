package com.cliniqo.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private boolean success;
    private T data;
    private ApiError error;
    private ApiMeta meta;

    public static <T> ApiResponse<T> success(T data, String requestId) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = true;
        response.data = data;
        response.meta = new ApiMeta(requestId);
        return response;
    }

    public static <T> ApiResponse<T> empty(String requestId) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = true;
        response.meta = new ApiMeta(requestId);
        return response;
    }

    public static <T> ApiResponse<T> error(ApiError error, String requestId) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = false;
        response.error = error;
        response.meta = new ApiMeta(requestId);
        return response;
    }

    public boolean isSuccess() { return success; }
    public T getData() { return data; }
    public ApiError getError() { return error; }
    public ApiMeta getMeta() { return meta; }
}
