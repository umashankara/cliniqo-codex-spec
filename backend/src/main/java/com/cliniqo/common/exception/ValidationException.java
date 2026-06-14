package com.cliniqo.common.exception;

import com.cliniqo.common.enums.ErrorCode;
import org.springframework.http.HttpStatus;

public class ValidationException extends ApiException {
    public ValidationException(String message) {
        super(ErrorCode.VALIDATION_ERROR, HttpStatus.BAD_REQUEST, message);
    }
}
