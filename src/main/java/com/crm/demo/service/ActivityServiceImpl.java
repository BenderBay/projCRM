package com.crm.demo.service;

import com.crm.demo.controller.dto.ActivityResponse;
import com.crm.demo.controller.dto.ActivityRequest;
import com.crm.demo.controller.dto.ActivityStatisticResponse;
import com.crm.demo.controller.exception.InvalidRequestException;
import com.crm.demo.controller.exception.ResourceNotFoundException;
import com.crm.demo.model.Activity;
import com.crm.demo.model.ActivityType;
import com.crm.demo.model.Contact;
import com.crm.demo.repository.ActivityRepository;
import com.crm.demo.repository.ContactRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class ActivityServiceImpl implements ActivityService {

    ActivityRepository activityRepo;
    ContactRepository contactRepo;

    public ActivityServiceImpl(ActivityRepository activityRepo, ContactRepository contactRepo) {
        this.activityRepo = activityRepo;
        this.contactRepo = contactRepo;
    }

    @Override
    public ActivityResponse getById(UUID id) {

        Objects.requireNonNull(id, "id must not be null");

        return ActivityResponse.from(activityRepo.findById(id).orElse(null));
    }

    @Override
    public List<ActivityResponse> getByContactId(UUID contactId) {

        Objects.requireNonNull(contactId, "contact id must not be null");

        Contact contact = contactRepo.findById(contactId).orElseThrow(() -> new ResourceNotFoundException("Contact not found"));

        return activityRepo.findByContactId(contact.getId(), Sort.by("timestamp").descending())
                .stream()
                .map(ActivityResponse::from)
                .toList();
    }

    @Override
    public ActivityResponse create(UUID contactId, ActivityRequest request) {

        Objects.requireNonNull(contactId, "contact id must not be null");
        Objects.requireNonNull(request, "ActivityRequest must not be null");

        if(request.getContactid() == null || !request.getContactid().equals(contactId)){
            throw new InvalidRequestException("contactid of first parameter and request should be not null and same");
        }

        Contact contact = contactRepo.findById(contactId).orElseThrow(() -> new ResourceNotFoundException("Contact not found"));

        if(!contact.isActive()){
            throw new ResourceNotFoundException("Contact not found");
        }

        Activity newCreated = activityRepo.save(Activity.create(contact, request.getType(), request.getDescription(), request.getTimestamp()));

        return ActivityResponse.from(newCreated);
    }

    @Override
    public ActivityStatisticResponse getContactStatisticForContactId(UUID contactId) {

        Objects.requireNonNull(contactId, "contact id must not be null");

        Contact contact = contactRepo.findById(contactId).orElseThrow(() -> new ResourceNotFoundException("Contact not found"));

        return ActivityStatisticResponse.from(
                contact.getId(),
                activityRepo.countByContactIdAndType(contact.getId(), ActivityType.CALL),
                activityRepo.countByContactIdAndType(contact.getId(), ActivityType.EMAIL),
                activityRepo.countByContactIdAndType(contact.getId(), ActivityType.MEETING)
        );
    }
}
