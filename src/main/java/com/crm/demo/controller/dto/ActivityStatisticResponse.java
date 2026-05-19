package com.crm.demo.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
public class ActivityStatisticResponse {

    @Getter
    UUID contactId;

    @Getter
    long contactTypeCallCount;

    @Getter
    long contactTypeEmailCount;

    @Getter
    long contactTypeMeetingCount;

    long summaryCount;

    public static ActivityStatisticResponse from(UUID contactId, long contactTypeCallCount, long contactTypeEmailCount, long contactTypeMeetingCount) {

        ActivityStatisticResponse response = new ActivityStatisticResponse();
        response.contactId  = contactId;
        response.contactTypeCallCount = contactTypeCallCount;
        response.contactTypeEmailCount = contactTypeEmailCount;
        response.contactTypeMeetingCount = contactTypeMeetingCount;

        return response;
    }

    public long getSummaryCount() {
        return contactTypeCallCount + contactTypeEmailCount + contactTypeMeetingCount;
    }

}