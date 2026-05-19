package com.crm.demo.controller.dto;

import com.crm.demo.model.Activity;
import com.crm.demo.model.ActivityType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ActivityResponse {

    UUID id;
    ActivityType type;
    String description;
    Instant timestamp;

    public static ActivityResponse from(Activity activity){

        if(activity == null){
            return null;
        }

        return new ActivityResponse(
                activity.getId(),
                activity.getType(),
                activity.getDescription(),
                activity.getTimestamp()
                );
    }

}
