package com.crm.demo.controller.dto;

import com.crm.demo.model.Contact;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ContactResponse {

    UUID id;
    String firstName;
    String lastName;
    String email;
    String company;
    boolean active;

    public static ContactResponse fromContact(Contact contact) {

       if (contact == null){
           return null;
       }

        return new ContactResponse(
                contact.getId(),
                contact.getFirstName(),
                contact.getLastName(),
                contact.getEmail(),
                contact.getCompany(),
                contact.isActive()
        );
    }
}
