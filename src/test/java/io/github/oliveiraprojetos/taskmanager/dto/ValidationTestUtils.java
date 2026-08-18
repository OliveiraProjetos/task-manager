package io.github.oliveiraprojetos.taskmanager.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public final class ValidationTestUtils {

    private static final ValidatorFactory VALIDATOR_FACTORY =
            Validation.buildDefaultValidatorFactory();

    private static final Validator VALIDATOR =
            VALIDATOR_FACTORY.getValidator();

    private ValidationTestUtils() {
    }

    public static Validator validator() {
        return VALIDATOR;
    }
}
