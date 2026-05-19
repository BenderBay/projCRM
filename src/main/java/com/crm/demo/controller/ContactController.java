package com.crm.demo.controller;

import com.crm.demo.controller.api.ContactApi;
import com.crm.demo.controller.dto.*;
import com.crm.demo.service.ActivityService;
import com.crm.demo.service.ContactService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(MappingRegistry.REQUEST_MAPPING_CONTACTS)
@CrossOrigin(origins = "*")
public class ContactController implements ContactApi {

    ContactService contactService;
    ActivityService activityService;

    public ContactController(ContactService contactService, ActivityService activityService) {
        this.contactService = contactService;
        this.activityService = activityService;
    }

    @Override
    public List<ContactResponse> getAll(boolean active) {
        return contactService.getAll(active);
    }


    @Override
    public ContactResponse getById(UUID id){
        return contactService.getById(id);
    }

    @Override
    public List<ActivityResponse> getContactActivities(UUID id){
        return activityService.getByContactId(id);
    }

    @Override
    public ActivityStatisticResponse getContactStatistics(UUID id){
        return activityService.getContactStatisticForContactId(id);
    }

    @Override
    public ResponseEntity<ActivityResponse> createActivity(UUID id, ActivityRequest request){
        ActivityResponse created = activityService.create(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Override
    public ResponseEntity<ContactResponse> create(CreateUpdateContactRequest request){
        ContactResponse created = contactService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Override
    public ResponseEntity<ContactResponse> update(UUID id, CreateUpdateContactRequest request){
        return ResponseEntity.ok(contactService.update(id,request));
    }

    @Override
    public ResponseEntity<Void> deactivate(UUID id) {
        contactService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}
