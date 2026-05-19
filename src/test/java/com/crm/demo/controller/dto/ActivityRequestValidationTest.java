package com.crm.demo.controller.dto;

import com.crm.demo.model.ActivityType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.SpringConstraintValidatorFactory;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class ActivityRequestValidationTest {

    private LocalValidatorFactoryBean validatorFactory;
    private Validator validator;

    @BeforeEach
    void setUp() {
        validatorFactory = new LocalValidatorFactoryBean();
        validatorFactory.setConstraintValidatorFactory(new SpringConstraintValidatorFactory(new DefaultListableBeanFactory()));
        validatorFactory.afterPropertiesSet();

        validator = validatorFactory.getValidator();
    }

    @AfterEach
    void tearDown() {
        validatorFactory.close();
    }

    @Test
    void validRequest_hasNoViolations() {
        ActivityRequest request = getValidRequest();
        Set<ConstraintViolation<ActivityRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }


    @Test
    void contactId_whenNull_hasViolation() {
        ActivityRequest request = getValidRequest();
        request.setContactid(null);

        Set<ConstraintViolation<ActivityRequest>> violations = validator.validate(request);

        assertThat(violations).anySatisfy(violation -> {
            assertThat(violation.getPropertyPath()).hasToString("contactid");
            assertThat(violation.getMessageTemplate()).isEqualTo("{jakarta.validation.constraints.NotNull.message}");
        });
    }

    @Test
    void tyope_whenNull_hasViolation() {
        ActivityRequest request = getValidRequest();
        request.setType(null);

        Set<ConstraintViolation<ActivityRequest>> violations = validator.validate(request);

        assertThat(violations).anySatisfy(violation -> {
            assertThat(violation.getPropertyPath()).hasToString("type");
            assertThat(violation.getMessageTemplate()).isEqualTo("{jakarta.validation.constraints.NotNull.message}");
        });
    }

    @Test
    void description_whenLongerThan150_hasViolation() {
        ActivityRequest request = getValidRequest();
        request.setDescription("a".repeat(251));

        Set<ConstraintViolation<ActivityRequest>> violations = validator.validate(request);

        assertThat(violations).anySatisfy(violation -> {
            assertThat(violation.getPropertyPath()).hasToString("description");
            assertThat(violation.getMessageTemplate()).isEqualTo("{jakarta.validation.constraints.Size.message}");
        });
    }


    @Test
    void timestamo_whenNull_hasViolation() {
        ActivityRequest request = getValidRequest();
        request.setTimestamp(null);

        Set<ConstraintViolation<ActivityRequest>> violations = validator.validate(request);

        assertThat(violations).anySatisfy(violation -> {
            assertThat(violation.getPropertyPath()).hasToString("timestamp");
            assertThat(violation.getMessageTemplate()).isEqualTo("{jakarta.validation.constraints.NotNull.message}");
        });
    }



    private ActivityRequest getValidRequest() {
        ActivityRequest request = new ActivityRequest();
        request.setContactid(UUID.randomUUID());
        request.setType(ActivityType.CALL);
        request.setDescription("testting call description");
        request.setTimestamp(Instant.now());

        return request;
    }

}
