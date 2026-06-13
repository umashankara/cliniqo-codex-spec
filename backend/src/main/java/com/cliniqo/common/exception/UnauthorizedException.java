package com.cliniqo.common.exception;

import com.cliniqo.common.enums.ErrorCode;
import org.springframework.http.HttpStatus;

public class UnauthorizedException extends ApiException {
    public UnauthorizedException(String message) {
        super(ErrorCode.UNAUTHENTICATED, HttpStatus.UNAUTHORIZED, message);
    }
}
