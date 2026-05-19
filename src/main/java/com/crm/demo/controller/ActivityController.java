package com.crm.demo.controller;


import com.crm.demo.controller.api.ActivityApi;
import com.crm.demo.controller.dto.ActivityResponse;
import com.crm.demo.service.ActivityService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(MappingRegistry.REQUEST_MAPPING_ACTIVITIES)
@CrossOrigin(origins = "*")
public class ActivityController implements ActivityApi {

    ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @Override
    public ActivityResponse getById(UUID id){
        return activityService.getById(id);
    }
}
