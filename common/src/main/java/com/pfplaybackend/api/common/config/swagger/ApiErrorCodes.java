package com.pfplaybackend.api.common.config.swagger;

import com.pfplaybackend.api.common.exception.DomainException;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 컨트롤러 메서드에 선언하여, 해당 API가 반환할 수 있는 도메인 에러를 Swagger에 자동 등록한다.
 * <p>
 * {@link DomainException}을 구현한 enum 클래스를 지정하면,
 * {@link ApiErrorCodeCustomizer}가 enum 상수의 errorCode, message, errorType을 읽어
 * HTTP 상태별로 그룹핑하여 Swagger 에러 응답과 Example을 자동 생성한다.
 * </p>
 *
 * <pre>
 * {@code @ApiErrorCodes({PartyroomException.class, PenaltyException.class})}
 * </pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiErrorCodes {
    Class<? extends DomainException>[] value();
}
