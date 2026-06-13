package com.cliniqo.common.exception;

import com.cliniqo.common.enums.ErrorCode;
import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends ApiException {
    public ResourceNotFoundException(String message) {
        super(ErrorCode.NOT_FOUND, HttpStatus.NOT_FOUND, message);
    }
}
