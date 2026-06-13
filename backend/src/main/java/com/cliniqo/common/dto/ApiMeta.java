package com.cliniqo.common.dto;

public class ApiMeta {
    private String requestId;

    public ApiMeta() {
    }

    public ApiMeta(String requestId) {
        this.requestId = requestId;
    }

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }
}
