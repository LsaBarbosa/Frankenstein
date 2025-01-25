package com.santanna.serviceorder.interfaces.handler;

import com.santanna.serviceorder.application.usecase.exception.OrderAlreadyExistsException;
import com.santanna.serviceorder.interfaces.handler.model.BadRequestException;
import com.santanna.serviceorder.interfaces.handler.model.InternalServerErrorException;
import com.santanna.serviceorder.interfaces.handler.model.NotFoundException;
import com.santanna.serviceorder.interfaces.handler.model.StandardError;
import com.santanna.serviceorder.application.utils.LoggerUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@ControllerAdvice
public class ResourceExceptionHandler {
    private final LoggerUtils loggerUtils;

    public ResourceExceptionHandler(LoggerUtils loggerUtils) {
        this.loggerUtils = loggerUtils;
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<StandardError> handleBadRequestException(BadRequestException ex, HttpServletRequest request) {
        loggerUtils.logWarn(ResourceExceptionHandler.class, "Bad request error: {} - Path: {}", ex.getMessage(), request.getRequestURI());

        var error = buildStandardError(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<StandardError> handleNotFoundException(NotFoundException ex, HttpServletRequest request) {
        loggerUtils.logWarn(ResourceExceptionHandler.class, "Resource not found: {} - Path: {}", ex.getMessage(), request.getRequestURI());

        var error = buildStandardError(HttpStatus.NOT_FOUND, ex.getMessage(), request);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(InternalServerErrorException.class)
    public ResponseEntity<StandardError> handleInternalServerErrorException(InternalServerErrorException ex, HttpServletRequest request) {
        loggerUtils.logError(ResourceExceptionHandler.class, "Internal server error: {} - Path: {}", ex, request.getRequestURI());

        var error = buildStandardError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<StandardError> handleAllExceptions(Exception ex, HttpServletRequest request) {
        loggerUtils.logError(ResourceExceptionHandler.class, "Unexpected error: {} - Path: {}", ex, request.getRequestURI());

        var error = buildStandardError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<StandardError> handleConstraintViolationException(ConstraintViolationException ex, HttpServletRequest request) {
        loggerUtils.logWarn(ResourceExceptionHandler.class, "Validation error: {} - Path: {}", ex.getMessage(), request.getRequestURI());

        var error = buildStandardError(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<StandardError> handleDataIntegrityViolationException(DataIntegrityViolationException ex, HttpServletRequest request) {
        loggerUtils.logWarn(ResourceExceptionHandler.class, "Data integrity violation: {} - Path: {}", ex.getMessage(), request.getRequestURI());

        var error = buildStandardError(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardError> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        loggerUtils.logWarn(ResourceExceptionHandler.class, "Validation failed at path: {}", request.getRequestURI());

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        });

        var error = buildStandardError(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }


    @ExceptionHandler(OrderAlreadyExistsException.class)
    public ResponseEntity<StandardError> handleOrderAlreadyExistsException(OrderAlreadyExistsException ex, HttpServletRequest request) {
        loggerUtils.logWarn(ResourceExceptionHandler.class, "Order already exists: {} - Path: {}", ex.getMessage(), request.getRequestURI());
        var error = buildStandardError(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

        private StandardError buildStandardError(HttpStatus status, String message, HttpServletRequest request) {
        return new StandardError(
                LocalDateTime.now(),
                status.value(),
                message,
                request.getRequestURI()
        );
    }

}
