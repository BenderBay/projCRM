package com.crm.demo.controller.dto;

import com.crm.demo.repository.ContactRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.SpringConstraintValidatorFactory;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ContactRequestValidationTest {

    @Mock
    private ContactRepository repo;
    private LocalValidatorFactoryBean validatorFactory;
    private Validator validator;

    @BeforeEach
    void setUp() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        beanFactory.registerSingleton("contactRepo", repo);

        validatorFactory = new LocalValidatorFactoryBean();
        validatorFactory.setConstraintValidatorFactory(new SpringConstraintValidatorFactory(beanFactory));
        validatorFactory.afterPropertiesSet();

        validator = validatorFactory.getValidator();
    }

    @AfterEach
    void tearDown() {
        validatorFactory.close();
    }

    @Test
    void validRequest_hasNoViolations() {
        CreateUpdateContactRequest request = getValidRequest();
        Set<ConstraintViolation<CreateUpdateContactRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    void fields_whenNull_hasViolation() {
        CreateUpdateContactRequest request = getValidRequest();
        request.setFirstName(null);
        request.setLastName(null);
        request.setEmail(null);

        Set<ConstraintViolation<CreateUpdateContactRequest>> violations = validator.validate(request);

        assertThat(violations)
                .anySatisfy(violation -> {
                    assertThat(violation.getPropertyPath()).hasToString("firstName");
                    assertThat(violation.getMessageTemplate()).isEqualTo("{jakarta.validation.constraints.NotNull.message}");
                }).anySatisfy(violation -> {
                    assertThat(violation.getPropertyPath()).hasToString("lastName");
                    assertThat(violation.getMessageTemplate()).isEqualTo("{jakarta.validation.constraints.NotNull.message}");
                }).anySatisfy(violation -> {
                    assertThat(violation.getPropertyPath()).hasToString("email");
                    assertThat(violation.getMessageTemplate()).isEqualTo("{jakarta.validation.constraints.NotNull.message}");
                });
    }

    @Test
    void fields_whenBlank_hasViolation() {
        CreateUpdateContactRequest request = getValidRequest();
        request.setFirstName("");
        request.setLastName("");
        request.setEmail("");

        Set<ConstraintViolation<CreateUpdateContactRequest>> violations = validator.validate(request);

        assertThat(violations)
                .anySatisfy(violation -> {
                    assertThat(violation.getPropertyPath()).hasToString("firstName");
                    assertThat(violation.getMessageTemplate()).isEqualTo("{jakarta.validation.constraints.NotBlank.message}");
                }).anySatisfy(violation -> {
                    assertThat(violation.getPropertyPath()).hasToString("lastName");
                    assertThat(violation.getMessageTemplate()).isEqualTo("{jakarta.validation.constraints.NotBlank.message}");
                }).anySatisfy(violation -> {
                    assertThat(violation.getPropertyPath()).hasToString("email");
                    assertThat(violation.getMessageTemplate()).isEqualTo("{jakarta.validation.constraints.NotBlank.message}");
                });
    }

    @Test
    void fields_whenTooLong_hasViolation() {
        CreateUpdateContactRequest request = getValidRequest();
        request.setFirstName("a".repeat(51));
        request.setLastName("a".repeat(51));
        request.setCompany("a".repeat(101));

        Set<ConstraintViolation<CreateUpdateContactRequest>> violations = validator.validate(request);

        assertThat(violations)
                .anySatisfy(violation -> {
                    assertThat(violation.getPropertyPath()).hasToString("firstName");
                    assertThat(violation.getMessageTemplate()).isEqualTo("{jakarta.validation.constraints.Size.message}");
                }).anySatisfy(violation -> {
                    assertThat(violation.getPropertyPath()).hasToString("lastName");
                    assertThat(violation.getMessageTemplate()).isEqualTo("{jakarta.validation.constraints.Size.message}");
                }).anySatisfy(violation -> {
                    assertThat(violation.getPropertyPath()).hasToString("company");
                    assertThat(violation.getMessageTemplate()).isEqualTo("{jakarta.validation.constraints.Size.message}");
                });
    }

    @Test
    void email_whenNotEmail_hasViolations() {
        CreateUpdateContactRequest request = getValidRequest();
        request.setEmail("rufff_yaards");   // works, but not good

        Set<ConstraintViolation<CreateUpdateContactRequest>> violations = validator.validate(request);

        assertThat(violations)
                .anySatisfy(violation -> {
                    assertThat(violation.getPropertyPath()).hasToString("email");
                    assertThat(violation.getMessageTemplate()).isEqualTo("{jakarta.validation.constraints.Email.message}");
                });
    }

    @Test
    void entity_whenNotUniqueContact_hasViolations() {

        CreateUpdateContactRequest request = getValidRequest();

        // mock that alreay exists one with same first/last/email combination and another id
        when(repo.existsByFirstNameAndLastNameAndEmailAndIdNot(any(), any(), any(), any())).thenReturn(true);

        Set<ConstraintViolation<CreateUpdateContactRequest>> violations = validator.validate(request);

        assertThat(violations)
                .anySatisfy(violation -> {
                    assertThat(violation.getPropertyPath()).hasToString("");
                    assertThat(violation.getMessageTemplate()).isEqualTo("Contact with first / lastname / email already exists");
                });
    }


    private CreateUpdateContactRequest getValidRequest() {
        CreateUpdateContactRequest request = new CreateUpdateContactRequest();
        request.setId(UUID.randomUUID());
        request.setFirstName("Mark");
        request.setLastName("Zuckerberg");
        request.setEmail("mz@facebook.de");
        request.setCompany("Facebook inc.");

        return request;
    }

}
