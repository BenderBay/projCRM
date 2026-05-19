package com.crm.demo.service;

import com.crm.demo.controller.dto.ActivityResponse;
import com.crm.demo.controller.dto.ActivityRequest;
import com.crm.demo.controller.dto.ActivityStatisticResponse;

import java.util.List;
import java.util.UUID;

public interface ActivityService {

    ActivityResponse getById(UUID id);

    List<ActivityResponse> getByContactId(UUID contactId);

    ActivityResponse create(UUID contactId, ActivityRequest request);

    ActivityStatisticResponse getContactStatisticForContactId(UUID contactId);
}
