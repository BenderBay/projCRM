package com.crm.demo.controller.dto.common;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UniqueContactValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueContact {

    String message() default "Contact with first / lastname / email already exists";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}