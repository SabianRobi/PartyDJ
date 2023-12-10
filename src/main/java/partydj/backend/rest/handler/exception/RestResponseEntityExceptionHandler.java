package partydj.backend.rest.handler.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import partydj.backend.rest.domain.error.*;
import partydj.backend.rest.domain.response.ErrorResponse;

import java.time.LocalDateTime;
import java.util.HashMap;

@ControllerAdvice
@SuppressWarnings("NullableProblems")
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = EntityNotFoundException.class)
    protected ResponseEntity<Object> handleEntityNotFoundException(final EntityNotFoundException ex,
                                                                   final WebRequest request) {
        return generateResponseBody(ex, new HttpHeaders(), HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(value = IllegalStateException.class)
    protected ResponseEntity<Object> handleIllegalStateException(final IllegalStateException ex,
                                                                 final WebRequest request) {
        return generateResponseBody(ex, new HttpHeaders(), HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    @ExceptionHandler(value = {RequiredFieldMissingException.class, RequiredFieldInvalidException.class})
    protected ResponseEntity<Object> handleRequiredFieldExceptions(final RequiredFieldException ex,
                                                                   final WebRequest request) {
        return generateResponseBody(ex, new HttpHeaders(), HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    protected ResponseEntity<Object> handleAccessDeniedException(final AccessDeniedException ex,
                                                                 final WebRequest request) {
        return generateResponseBody(ex, new HttpHeaders(), HttpStatus.FORBIDDEN, ex.getMessage(), request);
    }

    @ExceptionHandler(value = ThirdPartyApiException.class)
    protected ResponseEntity<Object> handleThirdPartyApiException(final ThirdPartyApiException ex,
                                                                  final WebRequest request) {
        return generateResponseBody(ex, new HttpHeaders(), HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage(), request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    private ResponseEntity<Object> handleConstraintViolationException(final ConstraintViolationException ex,
                                                                      final WebRequest request) {
        HashMap<String, String> errors = new HashMap<>();
        for (final ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            errors.put(violation.getPropertyPath().toString(), violation.getMessage());
        }

        return generateResponseBody(ex, new HttpHeaders(), HttpStatus.BAD_REQUEST, errors, request);
    }

    // Types are ok, but invalid
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex,
                                                                  final HttpHeaders headers,
                                                                  final HttpStatusCode status,
                                                                  final WebRequest request) {

        HashMap<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.put(error.getObjectName(), error.getDefaultMessage());
        }

        return generateResponseBody(ex, headers, status, errors, request);
    }

    // Not unique
    @ExceptionHandler(value = NotUniqueException.class)
    protected ResponseEntity<Object> handleNotUniqueExceptionException(final NotUniqueException ex,
                                                                       final WebRequest request) {
        HashMap<String, String> errors = new HashMap<>();
        errors.put(ex.getKey(), ex.getMessage());
        return generateResponseBody(ex, new HttpHeaders(), HttpStatus.CONFLICT, errors, request);
    }

    // Required GET parameter missing
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(final MissingServletRequestParameterException ex,
                                                                          final HttpHeaders headers,
                                                                          final HttpStatusCode status,
                                                                          final WebRequest request) {
        return generateResponseBody(ex, headers, status, ex.getMessage(), request);
    }

    private ResponseEntity<Object> generateResponseBody(final Exception ex,
                                                        final HttpHeaders headers,
                                                        final HttpStatusCode status,
                                                        final String error,
                                                        final WebRequest request) {
        final ErrorResponse responseBody = new ErrorResponse(status, error);
        return handleExceptionInternal(ex, responseBody, headers, status, request);
    }

    private ResponseEntity<Object> generateResponseBody(final Exception ex,
                                                        final HttpHeaders headers,
                                                        final HttpStatusCode status,
                                                        final HashMap<String, String> errors,
                                                        final WebRequest request) {
        ErrorResponse responseBody = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .errors(errors)
                .build();
        return handleExceptionInternal(ex, responseBody, headers, status, request);
    }
}