package partydj.backend.rest.handler.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import partydj.backend.rest.domain.error.RequiredFieldException;
import partydj.backend.rest.domain.error.RequiredFieldInvalidException;
import partydj.backend.rest.domain.error.RequiredFieldMissingException;
import partydj.backend.rest.domain.response.ErrorResponse;

import java.time.LocalDateTime;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = EntityNotFoundException.class)
    protected ResponseEntity<Object> handleEntityNotFoundException(final EntityNotFoundException ex,
                                                                   final WebRequest request) {
        return handleExceptions(ex, request, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = IllegalStateException.class)
    protected ResponseEntity<Object> handleIllegalStateException(final IllegalStateException ex, final WebRequest request) {
        return handleExceptions(ex, request, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = {RequiredFieldMissingException.class, RequiredFieldInvalidException.class})
    protected ResponseEntity<Object> handleRequiredFieldExceptions(final RequiredFieldException ex,
                                                                   final WebRequest request) {
        return handleExceptions(ex, request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    protected ResponseEntity<Object> handleAccessDeniedException(final AccessDeniedException ex,
                                                                   final WebRequest request) {
                                                                 final WebRequest request) {
        return handleExceptions(ex, request, HttpStatus.FORBIDDEN);
    }

    private ResponseEntity<Object> handleExceptions(final RuntimeException ex, final WebRequest request, final HttpStatus status) {
        final ErrorResponse responseBody = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(ex.getMessage())
                .build();
        return handleExceptionInternal(ex, responseBody, new HttpHeaders(), status, request);
    }
}