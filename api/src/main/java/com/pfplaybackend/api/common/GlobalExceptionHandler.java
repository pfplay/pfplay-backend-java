package com.pfplaybackend.api.common;

import com.pfplaybackend.api.enums.ExceptionEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

/**
 * 공통 handle Exception
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<?> handleNoSuchElementFoundException(NoSuchElementException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiCommonResponse.error(
                                ExceptionResult.builder()
                                        .code(ExceptionEnum.NO_SUCH_ELEMENT.getHttpStatusCode())
                                        .message(ExceptionEnum.NO_SUCH_ELEMENT.getMessage())
                                        .stackTrace(e.getMessage())
                                        .build()
                        )
                );
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public final ResponseEntity<?> handleDuplicateKeyException(DuplicateKeyException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiCommonResponse.error(
                                ExceptionResult.builder()
                                        .code(ExceptionEnum.DUPLICATE_KEY.getHttpStatusCode())
                                        .message(ExceptionEnum.DUPLICATE_KEY.getMessage())
                                        .stackTrace(e.getMessage())
                                        .build()
                        )
                );
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<?> handleAllException(Exception e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiCommonResponse.error(
                                ExceptionResult.builder()
                                        .code(ExceptionEnum.EXCEPTION.getHttpStatusCode())
                                        .message(ExceptionEnum.EXCEPTION.getMessage())
                                        .stackTrace(e.getMessage())
                                        .build()
                        )
                );
    }

}