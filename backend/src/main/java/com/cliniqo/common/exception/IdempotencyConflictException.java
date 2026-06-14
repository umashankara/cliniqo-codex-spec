package com.cliniqo.common.exception;

import com.cliniqo.common.enums.ErrorCode;
import org.springframework.http.HttpStatus;

public class IdempotencyConflictException extends ApiException {
    public IdempotencyConflictException(String message) {
        super(ErrorCode.IDEMPOTENCY_CONFLICT, HttpStatus.CONFLICT, message);
    }
}
