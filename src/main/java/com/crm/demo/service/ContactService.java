package com.crm.demo.service;

import com.crm.demo.controller.dto.ContactResponse;
import com.crm.demo.controller.dto.CreateUpdateContactRequest;

import java.util.List;
import java.util.UUID;

public interface ContactService {

    List<ContactResponse> getAll(boolean active);

    ContactResponse getById(UUID id);

    ContactResponse create(CreateUpdateContactRequest request);

    ContactResponse update(UUID id, CreateUpdateContactRequest request);

    ContactResponse deactivate(UUID id);


}
