package com.dd.auth.api.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.security.InvalidParameterException;
import java.util.*;

@RestControllerAdvice
public class ApplicationControllerAdvice extends ResponseEntityExceptionHandler {

    private final Logger logger = LogManager.getLogger();

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex) {
        return new ResponseEntity<>(ex.getConstraintViolations()
                .stream().findFirst()
                .get()
                .getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidParameterException.class)
    public ResponseEntity<Object> invalidRegistrationInput(InvalidParameterException invalidParameterException) {
        logger.error("Invalid registration parameters, pleas re-check");
        return new ResponseEntity<>(invalidParameterException.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserAuthenticationException.class)
    public ResponseEntity<Object> handleAuthenticationError(UserAuthenticationException authenticationException) {
        logger.error("Login authentication failed, please recheck credentials");
        return new ResponseEntity<>(authenticationException.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Object> handleBusinessException(BusinessException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handleNotFoundException(HttpServletRequest request,
                                                          NotFoundException ex) {
        logger.error("NotFoundException {}\n", request.getRequestURI(), ex);

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiCallError<>("Not found exception", Collections.singletonList(ex.getMessage())));
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
                                                                         HttpHeaders headers, HttpStatus status,
                                                                         WebRequest request) {
        return new ResponseEntity<>("Please provide valid http method type!", HttpStatus.METHOD_NOT_ALLOWED);
    }


    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiCallError<String>> handleValidationException(HttpServletRequest request,
                                                                          ValidationException ex) {
        logger.error("ValidationException {}\n", request.getRequestURI(), ex);

        return ResponseEntity
                .badRequest()
                .body(new ApiCallError<>("Validation exception", Collections.singletonList(ex.getMessage())));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiCallError<String>> handleMissingServletRequestParameterException(HttpServletRequest request,
                                                                                              MissingServletRequestParameterException ex) {
        logger.error("handleMissingServletRequestParameterException {}\n", request.getRequestURI(), ex);

        return ResponseEntity
                .badRequest()
                .body(new ApiCallError<>("Missing request parameter", Collections.singletonList(ex.getMessage())));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiCallError<Map<String, String>>> handleMethodArgumentTypeMismatchException(
            HttpServletRequest request, MethodArgumentTypeMismatchException ex) {
        logger.error("handleMethodArgumentTypeMismatchException {}\n", request.getRequestURI(), ex);

        Map<String, String> details = new HashMap<>();
        details.put("paramName", ex.getName());
        details.put("paramValue", Optional.ofNullable(ex.getValue()).map(Object::toString).orElse(""));
        details.put("errorMessage", ex.getMessage());

        return ResponseEntity
                .badRequest()
                .body(new ApiCallError<>("Method argument type mismatch", Collections.singletonList(details)));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiCallError<Map<String, String>>> handleMethodArgumentNotValidException(
            HttpServletRequest request, MethodArgumentNotValidException ex) {
        logger.error("handleMethodArgumentNotValidException {}\n", request.getRequestURI(), ex);

        List<Map<String, String>> details = new ArrayList<>();
        ex.getBindingResult()
                .getFieldErrors()
                .forEach(fieldError -> {
                    Map<String, String> detail = new HashMap<>();
                    detail.put("objectName", fieldError.getObjectName());
                    detail.put("field", fieldError.getField());
                    detail.put("rejectedValue", "" + fieldError.getRejectedValue());
                    detail.put("errorMessage", fieldError.getDefaultMessage());
                    details.add(detail);
                });

        return ResponseEntity
                .badRequest()
                .body(new ApiCallError<>("Method argument validation failed", details));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiCallError<String>> handleAccessDeniedException(HttpServletRequest request,
                                                                            AccessDeniedException ex) {
        logger.error("handleAccessDeniedException {}\n", request.getRequestURI(), ex);

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ApiCallError<>("Access denied!", Collections.singletonList(ex.getMessage())));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiCallError<String>> handleInternalServerError(HttpServletRequest request, Exception ex) {
        logger.error("handleInternalServerError {}\n", request.getRequestURI(), ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiCallError<>("Internal server error", Collections.singletonList(ex.getMessage())));
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApiCallError<T> {

        private String message;
        private List<T> details;

    }

}
