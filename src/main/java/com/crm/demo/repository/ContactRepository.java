package com.crm.demo.repository;

import com.crm.demo.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ContactRepository extends JpaRepository<Contact, UUID> {

    List<Contact> findByActive(boolean active);

    boolean existsByFirstNameAndLastNameAndEmailAndIdNot(String firstName, String lastName, String email, UUID id);
}
