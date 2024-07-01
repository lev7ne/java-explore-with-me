package ru.ewm.util.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


public class MainServiceAnnotation {
    /**
     * Кастомная аннотация NoEarlierThan2HoursBefore используется для валидации поля eventDate
     * в EventCreateDto и EventUpdateDto приходящих от пользователя/администратора.
     * <p>
     * Невозможно создать событие, поскольку дата события не может быть раньше, чем через 2 часа после текущего момента.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @Constraint(validatedBy = NoEarlierThan2HoursBeforeProcessor.class)
    public @interface NoEarlierThan2HoursBefore {
        String message() default
                "Cannot create the event because event date cannot be earlier than 2 hours after current moment";

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};
    }
}
