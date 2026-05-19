package com.crm.demo.controller.dto.common;

import com.crm.demo.controller.dto.CreateUpdateContactRequest;
import com.crm.demo.repository.ContactRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class UniqueContactValidator implements ConstraintValidator<UniqueContact, CreateUpdateContactRequest> {

    private final ContactRepository repo;

    public UniqueContactValidator(ContactRepository repo) {
        this.repo = repo;
    }

    @Override
    public boolean isValid(CreateUpdateContactRequest createReq, ConstraintValidatorContext context) {

        if (createReq == null) {
            return false;
        }

        return !repo.existsByFirstNameAndLastNameAndEmailAndIdNot(createReq.getFirstName(), createReq.getLastName(), createReq.getEmail(), createReq.getId());
    }
}
