package com.economia.quiz.api;

import com.economia.quiz.api.dto.ErrorResponse;
import com.economia.quiz.service.BusinessException;
import com.economia.quiz.service.NotFoundException;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException exception) {
        return error(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException exception) {
        return error(HttpStatus.UNPROCESSABLE_ENTITY, exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException exception) {
        List<String> details = exception.getBindingResult().getFieldErrors().stream()
                .map(field -> field.getField() + ": " + field.getDefaultMessage())
                .toList();
        return error(HttpStatus.BAD_REQUEST, "Requisicao invalida", details);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraint(ConstraintViolationException exception) {
        List<String> details = exception.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .toList();
        return error(HttpStatus.BAD_REQUEST, "Requisicao invalida", details);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException exception) {
        return error(HttpStatus.CONFLICT, "Violacao de integridade dos dados");
    }

    private ResponseEntity<ErrorResponse> error(HttpStatus status, String message) {
        return error(status, message, List.of(message));
    }

    private ResponseEntity<ErrorResponse> error(HttpStatus status, String message, List<String> details) {
        return ResponseEntity.status(status).body(new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                message,
                details
        ));
    }
}
