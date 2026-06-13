package com.cliniqo.common.dto;

import com.cliniqo.common.enums.ErrorCode;
import java.util.Map;

public class ApiError {
    private ErrorCode code;
    private String message;
    private Map<String, Object> details;

    public ApiError() {
    }

    public ApiError(ErrorCode code, String message, Map<String, Object> details) {
        this.code = code;
        this.message = message;
        this.details = details;
    }

    public ErrorCode getCode() { return code; }
    public void setCode(ErrorCode code) { this.code = code; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Map<String, Object> getDetails() { return details; }
    public void setDetails(Map<String, Object> details) { this.details = details; }
}
