package io.github.avinash2196.taskflowapipddlearninglab.exception;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;

/**
 * Centralizes controller error mapping so HTTP error responses stay consistent.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        FieldError fieldError = exception.getBindingResult().getFieldErrors().get(0);
        return buildBadRequest(fieldError.getDefaultMessage(), fieldError.getField());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolation(ConstraintViolationException exception) {
        ConstraintViolation<?> violation = exception.getConstraintViolations().iterator().next();
        String propertyPath = violation.getPropertyPath().toString();
        String fieldName = propertyPath.substring(propertyPath.lastIndexOf('.') + 1);
        return buildBadRequest(violation.getMessage(), fieldName);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ApiErrorResponse> handleHandlerMethodValidation(HandlerMethodValidationException exception) {
        ParameterValidationResult result = exception.getAllValidationResults().get(0);
        String fieldName = result.getMethodParameter().getParameterName();
        MessageSourceResolvable error = result.getResolvableErrors().get(0);
        String message = error.getDefaultMessage();
        return buildBadRequest(message, fieldName);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException exception) {
        return buildBadRequest("Request contains an unsupported value.", resolveInvalidFieldName(exception));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException exception) {
        return buildBadRequest("Request contains an unsupported value.", exception.getName());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiErrorResponse> handleMissingServletRequestParameter(MissingServletRequestParameterException exception) {
        return buildBadRequest(exception.getMessage(), exception.getParameterName());
    }

    @ExceptionHandler(ProjectNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleProjectNotFound(ProjectNotFoundException exception) {
        return buildNotFound("PROJECT_NOT_FOUND", exception.getMessage(), "projectId");
    }

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleTaskNotFound(TaskNotFoundException exception) {
        return buildNotFound("TASK_NOT_FOUND", exception.getMessage(), "taskId");
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiErrorResponse> handleLegacyNotFoundRuntimeExceptions(RuntimeException exception) {
        String simpleName = exception.getClass().getSimpleName();
        if ("ProjectNotFoundException".equals(simpleName)) {
            return buildNotFound("PROJECT_NOT_FOUND", exception.getMessage(), "projectId");
        }
        if ("TaskNotFoundException".equals(simpleName)) {
            return buildNotFound("TASK_NOT_FOUND", exception.getMessage(), "taskId");
        }
        throw exception;
    }

    private ResponseEntity<ApiErrorResponse> buildBadRequest(String message, String fieldName) {
        ApiErrorResponse response = new ApiErrorResponse(
                "VALIDATION_ERROR",
                message,
                Map.of("field", fieldName)
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    private ResponseEntity<ApiErrorResponse> buildNotFound(String errorCode, String message, String fieldName) {
        ApiErrorResponse response = new ApiErrorResponse(
                errorCode,
                message,
                Map.of("field", fieldName)
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    private String resolveInvalidFieldName(HttpMessageNotReadableException exception) {
        Throwable cause = exception.getCause();
        if (cause instanceof InvalidFormatException invalidFormatException
                && !invalidFormatException.getPath().isEmpty()) {
            JsonMappingException.Reference reference = invalidFormatException.getPath().get(0);
            if (reference.getFieldName() != null) {
                return reference.getFieldName();
            }
        }
        return "status";
    }
}
