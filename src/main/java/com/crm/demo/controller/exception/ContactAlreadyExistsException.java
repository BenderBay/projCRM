package com.crm.demo.controller.exception;

public class ContactAlreadyExistsException extends RuntimeException {

    public ContactAlreadyExistsException(String firstName, String lastName, String email) {

        super("Contact with combination of firstname, lastname, email already exists: " + firstName + " " + lastName  + " " + email);
    }
}
