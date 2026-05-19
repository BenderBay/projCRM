package com.crm.demo.service;

import com.crm.demo.controller.dto.ContactResponse;
import com.crm.demo.controller.dto.CreateUpdateContactRequest;
import com.crm.demo.controller.exception.ContactAlreadyExistsException;
import com.crm.demo.controller.exception.InvalidRequestException;
import com.crm.demo.controller.exception.ResourceNotFoundException;
import com.crm.demo.model.Contact;
import com.crm.demo.repository.ContactRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class ContactServiceImpl implements ContactService {

    private final ContactRepository repo;

    public ContactServiceImpl(ContactRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<ContactResponse> getAll(boolean active) {

        List<Contact> contacts = repo.findByActive(active);
        return contacts.stream().map(ContactResponse::fromContact).toList();
    }

    @Override
    public ContactResponse getById(UUID id) {
        Objects.requireNonNull(id, "id must not be null");

        Optional<Contact> contactOpt = repo.findById(id);

        return ContactResponse.fromContact(contactOpt.orElse(null));
    }


    @Override
    public ContactResponse create(CreateUpdateContactRequest request) {

        Objects.requireNonNull(request, "CreateUpdateContactRequest must not be null");


        if(request.getId() != null){
            throw new InvalidRequestException("Creation request should not have id");
        }

        if (repo.existsByFirstNameAndLastNameAndEmailAndIdNot(request.getFirstName(), request.getLastName(), request.getEmail(), request.getId())) {
            throw new ContactAlreadyExistsException(request.getFirstName(), request.getLastName(), request.getEmail());
        }

        Contact newContact = Contact.create(request.getFirstName(), request.getLastName(), request.getEmail(), request.getCompany());
        return ContactResponse.fromContact(repo.save(newContact));
    }

    @Override
    public ContactResponse update(UUID id, CreateUpdateContactRequest request) {

        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(request, "CreateUpdateContactRequest must not be null");

        if(request.getId() == null || !request.getId().equals(id)){
            throw new InvalidRequestException("Update request should have id");
        }

        if (repo.existsByFirstNameAndLastNameAndEmailAndIdNot(request.getFirstName(), request.getLastName(), request.getEmail(), request.getId())) {
            throw new ContactAlreadyExistsException(request.getFirstName(), request.getLastName(), request.getEmail());
        }

        Contact contact = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Contact not found"));

        contact.setFirstName(request.getFirstName());
        contact.setLastName(request.getLastName());
        contact.setEmail(request.getEmail());
        contact.setCompany(request.getCompany());

        return ContactResponse.fromContact(repo.save(contact));
    }

    @Override
    public ContactResponse deactivate(UUID id) {
        Objects.requireNonNull(id, "id must not be null");

        Contact contact = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Contact not found"));
        contact.setActive(false);  // idempotent: if already deactivated, will be done again
        contact = repo.save(contact);
        return ContactResponse.fromContact(contact);
    }
}
