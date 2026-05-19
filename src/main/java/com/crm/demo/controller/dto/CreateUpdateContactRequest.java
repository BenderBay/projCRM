package com.crm.demo.controller.dto;

import com.crm.demo.controller.dto.common.UniqueContact;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
@UniqueContact
public class CreateUpdateContactRequest {

    UUID id;

    @NotNull
    @NotBlank
    @Size(max = 50)
    String firstName;

    @NotNull
    @NotBlank
    @Size(max = 50)
    String lastName;

    @NotNull
    @NotBlank
    @Email
    String email;

    @Size(max = 100)
    String company;
}
