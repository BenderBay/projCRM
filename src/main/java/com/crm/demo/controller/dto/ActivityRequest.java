package com.crm.demo.controller.dto;

import com.crm.demo.model.ActivityType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class ActivityRequest{

    @NotNull
    UUID contactid;

    @NotNull
    ActivityType type;

    @Size(max = 150)
    String description;

    @NotNull
    Instant timestamp;

}
