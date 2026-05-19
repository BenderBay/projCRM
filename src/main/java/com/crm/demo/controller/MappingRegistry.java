package com.crm.demo.controller;

public interface MappingRegistry {

    // change version only of breaking changes
    String API_V1 = "/api/v1";

    String REQUEST_MAPPING_CONTACTS = API_V1 + "/contacts";

    String REQUEST_MAPPING_ACTIVITIES = API_V1 + "/activities";

}
