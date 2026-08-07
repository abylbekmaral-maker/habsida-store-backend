package com.project.exception;

import com.project.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleBadCredentialsException(
            BadCredentialsException ex, HttpServletRequest request) {

        return new ErrorResponse(
                LocalDateTime.now(),
                401,
                "Unauthorized",
                "Invalid username or password",
                request.getRequestURI(),
                null
        );
    }
    @ExceptionHandler(SecurityException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleSecurityException(SecurityException ex, HttpServletRequest request) {
        return new ErrorResponse(
                LocalDateTime.now(),
                403,
                "Forbidden",
                ex.getMessage(),
                request.getRequestURI(),
                null
        );
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleResourceNotFoundException(
            ResourceNotFoundException ex, HttpServletRequest request
    ) {
        return new ErrorResponse(
                LocalDateTime.now(),
                404,
                "Not Found",
                ex.getMessage(),
                request.getRequestURI(),
                null
        );
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConflictException(ConflictException ex, HttpServletRequest request) {
        return new ErrorResponse(
                LocalDateTime.now(),
                409,
                "Conflict",
                ex.getMessage(),
                request.getRequestURI(),
                null
        );
    }
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
        return new ErrorResponse(
                LocalDateTime.now(),
                403,
                "Forbidden",
                ex.getMessage(),
                request.getRequestURI(),
                null
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {

        List<ErrorResponse.FieldErrorDto> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new ErrorResponse.FieldErrorDto(error.getField(), error.getDefaultMessage()))
                .toList();
        return new ErrorResponse(
                LocalDateTime.now(),
                400,
                "Validation Error",
                "Input data validation error",
                request.getRequestURI(),
                fieldErrors
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleAllUncaughtException(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception occurred: ", ex);
        return new ErrorResponse(
                LocalDateTime.now(),
                500,
                "Internal Server Error",
                "Unexpected server error",
                request.getRequestURI(),
                null
        );
    }
}
