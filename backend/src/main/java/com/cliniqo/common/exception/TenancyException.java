package com.cliniqo.common.exception;

import com.cliniqo.common.enums.ErrorCode;
import org.springframework.http.HttpStatus;

public class TenancyException extends ApiException {
    public TenancyException(String message) {
        super(ErrorCode.TENANCY_ERROR, HttpStatus.BAD_REQUEST, message);
    }
}
