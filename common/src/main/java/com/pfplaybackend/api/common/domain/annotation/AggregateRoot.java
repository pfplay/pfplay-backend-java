package com.pfplaybackend.api.common.domain.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Aggregate Root 마커 어노테이션.
 *
 * <p>이 어노테이션이 부착된 엔티티는 해당 Aggregate의 루트이며,
 * 외부에서 Aggregate 내부 엔티티에 직접 접근하지 않고
 * 반드시 이 루트를 통해서만 접근해야 한다.</p>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AggregateRoot {
}
