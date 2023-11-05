package partydj.backend.rest.handler.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import partydj.backend.rest.domain.response.ErrorResponse;

import java.time.LocalDateTime;

@ControllerAdvice
public class RestResponseEntityExceptionHandler
        extends ResponseEntityExceptionHandler {

//    @ExceptionHandler(value = UnrecognizedPropertyException.class)
//    protected ResponseEntity<Object> handleUnrecognizedPropertyException(final UnrecognizedPropertyException ex,
//                                                                         final WebRequest request) {
//        final String responseBody = ex.getMessage();
//        return handleExceptionInternal(ex, responseBody, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
//    }

    @ExceptionHandler(value = EntityNotFoundException.class)
    protected ResponseEntity<Object> handleEntityNotFoundException(final EntityNotFoundException ex,
                                                                   final WebRequest request) {
        final ErrorResponse responseBody = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error(ex.getMessage())
                .build();
        return handleExceptionInternal(ex, responseBody, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }
}