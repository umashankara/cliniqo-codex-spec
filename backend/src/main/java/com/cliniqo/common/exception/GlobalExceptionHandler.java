package com.cliniqo.common.exception;

import com.cliniqo.common.dto.ApiError;
import com.cliniqo.common.dto.ApiResponse;
import com.cliniqo.common.enums.ErrorCode;
import com.cliniqo.config.RequestIdFilter;
import jakarta.validation.ConstraintViolationException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<Void>> handleApiException(ApiException ex) {
        ApiError error = new ApiError(ex.getErrorCode(), safeMessage(ex), null);
        return ResponseEntity.status(ex.getStatus()).body(ApiResponse.error(error, RequestIdFilter.currentRequestId()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, Object> details = ex.getBindingResult().getFieldErrors().stream()
                .collect(java.util.stream.Collectors.toMap(
                        FieldError::getField,
                        fieldError -> String.valueOf(fieldError.getDefaultMessage()),
                        (left, right) -> left));
        ApiError error = new ApiError(ErrorCode.VALIDATION_ERROR, "Request validation failed", details);
        return ResponseEntity.badRequest().body(ApiResponse.error(error, RequestIdFilter.currentRequestId()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(ConstraintViolationException ex) {
        ApiError error = new ApiError(ErrorCode.VALIDATION_ERROR, "Request validation failed", null);
        return ResponseEntity.badRequest().body(ApiResponse.error(error, RequestIdFilter.currentRequestId()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpected(Exception ex) {
        log.error("Unhandled request failure: {}", ex.getClass().getSimpleName());
        ApiError error = new ApiError(ErrorCode.INTERNAL_ERROR, "Unexpected server error", null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(error, RequestIdFilter.currentRequestId()));
    }

    private String safeMessage(ApiException ex) {
        if (ex.getErrorCode() == ErrorCode.NOT_FOUND) {
            return "Resource not found";
        }
        return ex.getMessage();
    }
}
