package com.dd.auth.api.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;
import java.security.InvalidParameterException;
import java.util.*;

@RestControllerAdvice
public class ApplicationControllerAdvice {

    private final Logger logger = LogManager.getLogger(ApplicationControllerAdvice.class);

    @ExceptionHandler({DataIntegrityViolationException.class})
    public ResponseEntity<Object> handleConstraintViolation(HttpServletRequest request, DataIntegrityViolationException ex) {
        logger.error("SQL query data violation, pleas re-check{}\n", request.getRequestURI(), ex);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiCallError<>(HttpStatus.BAD_REQUEST, Collections.singletonList(ex.getMessage()), "Failed"));
    }

    @ExceptionHandler(InvalidParameterException.class)
    public ResponseEntity<Object> invalidRegistrationInput(HttpServletRequest request, InvalidParameterException ex) {
        logger.error("Invalid registration parameters, pleas re-check{}\n", request.getRequestURI(), ex);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiCallError<>(HttpStatus.BAD_REQUEST, Collections.singletonList(ex.getMessage())));
    }

    @ExceptionHandler(UserAuthenticationException.class)
    public ResponseEntity<Object> handleAuthenticationError(HttpServletRequest request, UserAuthenticationException ex) {
        logger.error("Login authentication failed, please recheck credentials {}\n", request.getRequestURI(), ex);
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ApiCallError<>(HttpStatus.FORBIDDEN, Collections.singletonList(ex.getMessage())));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Object> handleBusinessException(HttpServletRequest request, BusinessException ex) {
        logger.error("No content {}\n", request.getRequestURI(), ex);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(new ApiCallError<>(HttpStatus.NO_CONTENT, Collections.singletonList(ex.getMessage())));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handleNotFoundException(HttpServletRequest request,
                                                          NotFoundException ex) {
        logger.error("NotFoundException {}\n", request.getRequestURI(), ex);

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiCallError<>(HttpStatus.NOT_FOUND, Collections.singletonList(ex.getMessage())));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiCallError<String>> handleValidationException(HttpServletRequest request,
                                                                          ValidationException ex) {
        logger.error("ValidationException {}\n", request.getRequestURI(), ex);

        return ResponseEntity
                .badRequest()
                .body(new ApiCallError<>(HttpStatus.BAD_REQUEST, Collections.singletonList(ex.getMessage())));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiCallError<String>> handleMissingServletRequestParameterException(HttpServletRequest request,
                                                                                              MissingServletRequestParameterException ex) {
        logger.error("handleMissingServletRequestParameterException {}\n", request.getRequestURI(), ex);

        return ResponseEntity
                .badRequest()
                .body(new ApiCallError<>(HttpStatus.BAD_REQUEST, Collections.singletonList(ex.getMessage())));
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
                .body(new ApiCallError<>(HttpStatus.BAD_REQUEST, Collections.singletonList(details)));
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
                .body(new ApiCallError<>(HttpStatus.BAD_REQUEST, details));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiCallError<String>> handleAccessDeniedException(HttpServletRequest request,
                                                                            AccessDeniedException ex) {
        logger.error("handleAccessDeniedException {}\n", request.getRequestURI(), ex);

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ApiCallError<>(HttpStatus.FORBIDDEN, Collections.singletonList(ex.getMessage())));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiCallError<String>> handleInternalServerError(HttpServletRequest request, Exception ex) {
        logger.error("handleInternalServerError {}\n", request.getRequestURI(), ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiCallError<>(HttpStatus.INTERNAL_SERVER_ERROR, Collections.singletonList(ex.getMessage())));
    }

    @Data
    @AllArgsConstructor
    public static class ApiCallError<T> {

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
        private Date timestamp;

        private String status;
        private String stackTrace;
        private int code;
        private List<T> details;

        public ApiCallError() {
            timestamp = new Date();
        }

        public ApiCallError(HttpStatus httpStatus, List<T> data) {
            this();
            this.code = httpStatus.value();
            this.details = data;
        }

        public ApiCallError(HttpStatus httpStatus, List<T> data, String status) {
            this(httpStatus, data);
            this.status = status;
        }

    }

}
