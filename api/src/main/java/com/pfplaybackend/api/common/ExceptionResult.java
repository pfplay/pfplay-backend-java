package com.pfplaybackend.api.common;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Schema(description = "Exception Result")
@Getter
@Builder
@AllArgsConstructor
public class ExceptionResult {
    @Schema(implementation = HttpStatus.class)
    private HttpStatus status;
    private int code;
    private String message;
}
