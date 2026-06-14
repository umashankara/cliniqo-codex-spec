package com.cliniqo.common.exception;

import com.cliniqo.common.enums.ErrorCode;
import org.springframework.http.HttpStatus;

public class ClinicInactiveException extends ApiException {
    public ClinicInactiveException() {
        super(ErrorCode.CLINIC_INACTIVE, HttpStatus.UNAUTHORIZED, "Clinic is inactive");
    }
}
