package com.pfplaybackend.api.auth.validation;

import com.pfplaybackend.api.auth.enums.OAuthProvider;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class ProviderValidator implements ConstraintValidator<ValidProvider, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return false;
        }

        return Arrays.stream(OAuthProvider.values())
                .anyMatch(provider -> provider.name().equalsIgnoreCase(value));
    }
}
