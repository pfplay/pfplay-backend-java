package com.pfplaybackend.api.pfplay.partyroom;

import com.pfplaybackend.api.partyroom.presentation.request.PartyRoomCreateRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;


@SpringBootTest
@ActiveProfiles("test")
class PartyRoomValidTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    public static void init() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Test
    public void partyRoomRequestValidation() {
        PartyRoomCreateRequest partyRoomCreateRequest = new PartyRoomCreateRequest(
                "뉴진스!!", "", "https://pfplay.io", 3
        );

        Set<ConstraintViolation<PartyRoomCreateRequest>> violations = validator.validate(partyRoomCreateRequest);
        Assertions.assertFalse(violations.isEmpty());
    }

}