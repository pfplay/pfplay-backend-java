package com.pfplaybackend.api.auth.adapter.in.web.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ProviderValidator.class)
@Documented
public @interface ValidProvider {
    String message() default "Invalid OAuth provider. Must be 'google' or 'twitter'";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
