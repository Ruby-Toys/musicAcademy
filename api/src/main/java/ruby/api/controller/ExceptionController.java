package ruby.api.controller;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ruby.api.exception.BusinessException;
import ruby.api.response.ErrorResponse;

@RestControllerAdvice
public class ExceptionController {

    public static final String BIND_EXCEPTION_MESSAGE = "형식에 맞지 않는 값이 존재합니다.";
    public static final String NOT_FOUND_MESSAGE = "해당 데이터가 존재하지 않습니다.";

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public ErrorResponse bindExceptionHandler(BindException e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(BIND_EXCEPTION_MESSAGE)
                .build();

        e.getFieldErrors().forEach(errorResponse::addValidation);
        return errorResponse;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({InvalidFormatException.class, IllegalArgumentException.class})
    public ErrorResponse invalidFormatExceptionHandler(Exception e) {
        return ErrorResponse.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(BIND_EXCEPTION_MESSAGE)
                .build();
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<ErrorResponse> emptyResultDataAccessExceptionHandler(EmptyResultDataAccessException e) {
        int status = HttpStatus.NOT_FOUND.value();
        ErrorResponse body = ErrorResponse.builder()
                .code(status)
                .message(NOT_FOUND_MESSAGE)
                .build();

        return ResponseEntity.status(status).body(body);
    }

    /**
     * 비즈니스 처리 중 예외 발생시 처리
     * @param e
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> businessExceptionHandler(BusinessException e) {
        int status = e.getStatusCode();
        ErrorResponse body = ErrorResponse.builder()
                .code(status)
                .message(e.getMessage())
                .build();

        return ResponseEntity.status(status).body(body);
    }
}
