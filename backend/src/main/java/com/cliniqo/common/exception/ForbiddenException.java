package com.cliniqo.common.exception;

import com.cliniqo.common.enums.ErrorCode;
import org.springframework.http.HttpStatus;

public class ForbiddenException extends ApiException {
    public ForbiddenException(String message) {
        super(ErrorCode.FORBIDDEN, HttpStatus.FORBIDDEN, message);
    }
}
