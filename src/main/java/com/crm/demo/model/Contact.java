package com.crm.demo.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "contact",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"firstName", "lastName", "email"})
        })
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "UUID", nullable = false)
    private UUID id;

    @Column(nullable = false, length = 250)
    @Setter
    String firstName;

    @Column(nullable = false, length = 250)
    @Setter
    String lastName;

    @Setter
    @Column(nullable = false, length = 250)
    String email;

    @Setter
    @Column(nullable = true, length = 250)
    String company;

    @Column(nullable = false)
    @Setter
    boolean active = true;


    public static Contact create(String firstName, String lastName, String email, String company) {
        Contact newContact = new Contact();
        newContact.setFirstName(firstName);
        newContact.setLastName(lastName);
        newContact.setEmail(email);
        newContact.setCompany(company);

        return newContact;
    }

}
