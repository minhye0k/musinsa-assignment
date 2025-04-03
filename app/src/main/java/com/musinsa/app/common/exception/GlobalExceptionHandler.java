package com.musinsa.app.common.exception;

import com.musinsa.common.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ExceptionResponse> handle(CustomException e) {
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .message(e.getMessage())
                .build();

        return ResponseEntity.status(e.getStatusCode()).body(exceptionResponse);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handle(Exception e) {
        Throwable rootCause = findRootCause(e);

        if (rootCause instanceof CustomException ce) {
            ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                    .message(ce.getMessage())
                    .build();
            return ResponseEntity.status(ce.getStatusCode()).body(exceptionResponse);
        }

        log.info(e.getMessage());

        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .message("서버에 문제가 발생하였습니다. 재시도후 문제가 지속되면 고객센터로 문의 바랍니다.")
                .build();

        return ResponseEntity.internalServerError().body(exceptionResponse);
    }

    private static Throwable findRootCause(Throwable e) {
        Throwable cause = e;
        while (cause.getCause() != null && cause.getCause() != cause) {
            cause = cause.getCause();
        }
        return cause;
    }
}
