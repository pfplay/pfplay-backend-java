package com.pfplaybackend.api.common;

import com.pfplaybackend.api.common.enums.ExceptionEnum;
import com.pfplaybackend.api.partyroom.exception.UnsupportedChatMessageTypeException;
import com.pfplaybackend.api.partyroom.exception.UnsupportedChatRequestException;
import com.pfplaybackend.api.playlist.exception.InvalidDeleteRequestException;
import com.pfplaybackend.api.playlist.exception.PlaylistLimitExceededException;
import com.pfplaybackend.api.playlist.exception.PlaylistMusicLimitExceededException;
import com.pfplaybackend.api.playlist.exception.PlaylistNoWalletException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

/**
 * 공통 handle Exception
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // @TODO custom exception 모듈화 리팩토링 필요
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<?> handleNoSuchElementFoundException(NoSuchElementException e) {
        log.error(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiCommonResponse.error(
                                ExceptionResult.builder()
                                        .code(ExceptionEnum.NO_SUCH_ELEMENT.getHttpStatusCode())
                                        .message(e.getMessage())
                                        .build()
                        )
                );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException e) {
        log.error(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiCommonResponse.error(
                                ExceptionResult.builder()
                                        .code(ExceptionEnum.ACCESS_DENIED_EXCEPTION.getHttpStatusCode())
                                        .message(ExceptionEnum.ACCESS_DENIED_EXCEPTION.getMessage())
                                        .build()
                        )
                );
    }

    @ExceptionHandler(PlaylistNoWalletException.class)
    public ResponseEntity<?> handlePlaylistNoWalletException(PlaylistNoWalletException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiCommonResponse.error(
                                ExceptionResult.builder()
                                        .errorCode("BR001")
                                        .message(e.getMessage())
                                        .build()
                        )
                );
    }

    @ExceptionHandler(PlaylistLimitExceededException.class)
    public ResponseEntity<?> handlePlaylistLimitExceededException(PlaylistLimitExceededException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiCommonResponse.error(
                                ExceptionResult.builder()
                                        .errorCode("BR002")
                                        .message(e.getMessage())
                                        .build()
                        )
                );
    }

    @ExceptionHandler(PlaylistMusicLimitExceededException.class)
    public ResponseEntity<?> handlePlaylistMusicLimitExceededException(PlaylistMusicLimitExceededException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiCommonResponse.error(
                                ExceptionResult.builder()
//                                        .status() ResponseEntity HttpStatusCode 의미 중복되므로 불필요
//                                        .code() ResponseEntity HttpStatusCode 의미 중복되므로 불필요
                                        .message(e.getMessage())
                                        .build()
                        )
                );
    }

    @ExceptionHandler(InvalidDeleteRequestException.class)
    public ResponseEntity<?> handleInvalidDeleteRequestException(InvalidDeleteRequestException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiCommonResponse.error(
                                ExceptionResult.builder()
                                        .message(e.getMessage())
                                        .build()
                        )
                );
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public final ResponseEntity<?> handleDuplicateKeyException(DuplicateKeyException e) {
        log.error(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiCommonResponse.error(
                                ExceptionResult.builder()
                                        .code(ExceptionEnum.DUPLICATE_KEY.getHttpStatusCode())
                                        .message(ExceptionEnum.DUPLICATE_KEY.getMessage())
                                        .build()
                        )
                );
    }

    @ExceptionHandler(RuntimeException.class)
    public final ResponseEntity<?> handleRuntimeException(RuntimeException e) {
        log.error(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiCommonResponse.error(
                                ExceptionResult.builder()
                                        .code(ExceptionEnum.EXCEPTION.getHttpStatusCode())
                                        .message(e.getMessage())
                                        .build()
                        )
                );
    }
    @ExceptionHandler(Exception.class)
    public final ResponseEntity<?> handleAllException(Exception e) {
        log.error(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiCommonResponse.error(
                        ExceptionResult.builder()
                                .code(ExceptionEnum.EXCEPTION.getHttpStatusCode())
                                .message(ExceptionEnum.EXCEPTION.getMessage())
                                .build()
                        )
                );
    }

    @ExceptionHandler(UnsupportedChatMessageTypeException.class)
    public final ResponseEntity<?> handleUnsupportedChatMessageTypeException(UnsupportedChatMessageTypeException e) {
        log.error(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiCommonResponse.error(
                        ExceptionResult.builder()
                                .code(ExceptionEnum.EXCEPTION.getHttpStatusCode())
                                .message(ExceptionEnum.EXCEPTION.getMessage())
                                .build()
                        )
                );

    }

    @ExceptionHandler(UnsupportedChatRequestException.class)
    public final ResponseEntity<?> handleUnsupportedChatRequestException(UnsupportedChatRequestException e) {
        log.error(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiCommonResponse.error(
                                ExceptionResult.builder()
                                        .code(ExceptionEnum.EXCEPTION.getHttpStatusCode())
                                        .message(ExceptionEnum.EXCEPTION.getMessage())
                                        .build()
                        )
                );
    }

}