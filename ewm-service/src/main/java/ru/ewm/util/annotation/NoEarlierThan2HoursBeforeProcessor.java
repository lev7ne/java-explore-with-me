package ru.ewm.util.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;


public class NoEarlierThan2HoursBeforeProcessor implements ConstraintValidator<MainServiceAnnotation.NoEarlierThan2HoursBefore, LocalDateTime> {
    @Override
    public void initialize(MainServiceAnnotation.NoEarlierThan2HoursBefore constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(LocalDateTime localDateTime, ConstraintValidatorContext constraintValidatorContext) {
        return localDateTime == null || localDateTime.isAfter(LocalDateTime.now().plusHours(2));
    }
}
