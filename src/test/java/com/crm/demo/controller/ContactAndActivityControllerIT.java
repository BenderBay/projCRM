package com.crm.demo.controller;

import com.crm.demo.controller.dto.*;
import com.crm.demo.model.ActivityType;
import com.crm.demo.repository.ContactRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.jpa.hibernate.ddl-auto=create"
        }
)
@Testcontainers
class ContactAndActivityControllerIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withEnv("POSTGRES_INITDB_ARGS", "--encoding=UTF8 --locale=de_DE.UTF-8");

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ContactRepository contactRepo;

    @AfterEach
    void tearDown() {
        contactRepo.deleteAll(); // Activities alle be deleted by cascading
    }

    @Test
    void contact_crud_ok() {
        // Create
        CreateUpdateContactRequest createUpdateRequest = createContactRequest(null, "Sven", "Müller", "sm@facebook.de", "Facebook Inc.");

        ResponseEntity<ContactResponse> createResponse = restTemplate.exchange(
                MappingRegistry.REQUEST_MAPPING_CONTACTS,
                HttpMethod.POST,
                new HttpEntity<>(createUpdateRequest),
                ContactResponse.class
        );

        assertThat(createResponse.getStatusCode()).isEqualTo(CREATED);
        assertThat(createResponse.getHeaders().getContentType()).isNotNull().isEqualTo(MediaType.APPLICATION_JSON);

        ContactResponse createResponseBody = createResponse.getBody();
        assertThat(createResponseBody).isNotNull();
        assertThat(createResponseBody.getId()).isNotNull();
        assertThat(createResponseBody.getFirstName()).isEqualTo(createUpdateRequest.getFirstName());
        assertThat(createResponseBody.getLastName()).isEqualTo(createUpdateRequest.getLastName());
        assertThat(createResponseBody.getEmail()).isEqualTo(createUpdateRequest.getEmail());
        assertThat(createResponseBody.getCompany()).isEqualTo(createUpdateRequest.getCompany());

        // Read using getAll(true) and getAll(false)
        ResponseEntity<List<ContactResponse>> readResponse = restTemplate.exchange(
                MappingRegistry.REQUEST_MAPPING_CONTACTS + "?active={active}",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                },
                true
        );

        assertThat(readResponse.getStatusCode()).isEqualTo(OK);
        assertThat(readResponse.getHeaders().getContentType()).isNotNull().isEqualTo(MediaType.APPLICATION_JSON);

        assertThat(readResponse.getBody()).isNotNull().hasSize(1);

        readResponse = restTemplate.exchange(
                MappingRegistry.REQUEST_MAPPING_CONTACTS + "?active={active}",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                },
                false
        );

        assertThat(readResponse.getStatusCode()).isEqualTo(OK);
        assertThat(readResponse.getHeaders().getContentType()).isNotNull().isEqualTo(MediaType.APPLICATION_JSON);

        assertThat(readResponse.getBody()).isNotNull().hasSize(0);

        // Read using getById()
        ResponseEntity<ContactResponse> readResponse2 = restTemplate.exchange(
                MappingRegistry.REQUEST_MAPPING_CONTACTS + "/{id}",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                },
                createResponseBody.getId()
        );

        assertThat(readResponse2.getStatusCode()).isEqualTo(OK);
        assertThat(readResponse2.getHeaders().getContentType()).isNotNull().isEqualTo(MediaType.APPLICATION_JSON);

        assertThat(readResponse2.getBody()).isNotNull();
        assertThat(readResponse2.getBody().getId()).isNotNull();
        assertThat(readResponse2.getBody().getFirstName()).isEqualTo(createUpdateRequest.getFirstName());
        assertThat(readResponse2.getBody().getLastName()).isEqualTo(createUpdateRequest.getLastName());
        assertThat(readResponse2.getBody().getEmail()).isEqualTo(createUpdateRequest.getEmail());
        assertThat(readResponse2.getBody().getCompany()).isEqualTo(createUpdateRequest.getCompany());

        // Update
        createUpdateRequest.setId(createResponse.getBody().getId());
        createUpdateRequest.setFirstName("Ilya");

        ResponseEntity<ContactResponse> updateResponse = restTemplate.exchange(
                MappingRegistry.REQUEST_MAPPING_CONTACTS + "/{id}",
                HttpMethod.PUT,
                new HttpEntity<>(createUpdateRequest),
                new ParameterizedTypeReference<>() {
                },
                createResponseBody.getId()
        );

        assertThat(updateResponse.getStatusCode()).isEqualTo(OK);
        assertThat(updateResponse.getHeaders().getContentType()).isNotNull().isEqualTo(MediaType.APPLICATION_JSON);

        ContactResponse updateResponseBody = updateResponse.getBody();
        assertThat(updateResponseBody).isNotNull();
        assertThat(updateResponseBody.getId()).isNotNull();
        assertThat(updateResponseBody.getFirstName()).isEqualTo(createUpdateRequest.getFirstName()).isEqualTo("Ilya");
        assertThat(updateResponseBody.getLastName()).isEqualTo(createUpdateRequest.getLastName());
        assertThat(updateResponseBody.getEmail()).isEqualTo(createUpdateRequest.getEmail());
        assertThat(updateResponseBody.getCompany()).isEqualTo(createUpdateRequest.getCompany());

        // Check if updated
        ResponseEntity<ContactResponse> readResponse3 = restTemplate.exchange(
                MappingRegistry.REQUEST_MAPPING_CONTACTS + "/{id}",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                },
                createResponseBody.getId()
        );

        assertThat(readResponse3.getStatusCode()).isEqualTo(OK);
        assertThat(readResponse3.getHeaders().getContentType()).isNotNull().isEqualTo(MediaType.APPLICATION_JSON);

        assertThat(readResponse3.getBody()).isNotNull();
        assertThat(readResponse3.getBody().getId()).isNotNull();
        assertThat(readResponse3.getBody().getFirstName()).isEqualTo(createUpdateRequest.getFirstName()).isEqualTo("Ilya");
        assertThat(readResponse3.getBody().isActive()).isTrue();

        // Delete deactivate()
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                MappingRegistry.REQUEST_MAPPING_CONTACTS + "/{id}",
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                Void.class,
                createResponseBody.getId()
        );

        assertThat(deleteResponse.getStatusCode()).isEqualTo(NO_CONTENT);

        // Check if deleted
        ResponseEntity<ContactResponse> readResponse4 = restTemplate.exchange(
                MappingRegistry.REQUEST_MAPPING_CONTACTS + "/{id}",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                },
                createResponseBody.getId()
        );

        assertThat(readResponse4.getBody().isActive()).isFalse();
    }

    @Test
    void contact_activity_crud_ok() {

        // Create
        CreateUpdateContactRequest createContactRequest = createContactRequest(null, "Sven", "Müller", "sm@facebook.de", "Facebook Inc.");

        ResponseEntity<ContactResponse> createContactResponse = restTemplate.exchange(
                MappingRegistry.REQUEST_MAPPING_CONTACTS,
                HttpMethod.POST,
                new HttpEntity<>(createContactRequest),
                ContactResponse.class
        );

        assertThat(createContactResponse.getStatusCode()).isEqualTo(CREATED);
        assertThat(createContactResponse.getHeaders().getContentType()).isNotNull().isEqualTo(MediaType.APPLICATION_JSON);

        ContactResponse createResponseBody = createContactResponse.getBody();
        assertThat(createResponseBody).isNotNull();
        assertThat(createResponseBody.getId()).isNotNull();

        ActivityRequest createActivityRequest = createActivityRequest(createResponseBody.getId(), ActivityType.CALL, "Cold aquise call", Instant.now());

        ResponseEntity<ActivityResponse> createActivityResponse = restTemplate.exchange(
                MappingRegistry.REQUEST_MAPPING_CONTACTS + "/{id}/activities",
                HttpMethod.POST,
                new HttpEntity<>(createActivityRequest),
                ActivityResponse.class,
                createResponseBody.getId()
        );
        assertThat(createActivityResponse.getStatusCode()).isEqualTo(CREATED);
        assertThat(createActivityResponse.getHeaders().getContentType()).isNotNull().isEqualTo(MediaType.APPLICATION_JSON);

        assertThat(createActivityResponse.getBody()).isNotNull();
        assertThat(createActivityResponse.getBody().getId()).isNotNull();

        // Read all contact activities
        ResponseEntity<List<ActivityResponse>> readResponse = restTemplate.exchange(
                MappingRegistry.REQUEST_MAPPING_CONTACTS + "/{id}/activities",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                },
                createResponseBody.getId()
        );

        assertThat(readResponse.getStatusCode()).isEqualTo(OK);
        assertThat(readResponse.getHeaders().getContentType()).isNotNull().isEqualTo(MediaType.APPLICATION_JSON);

        assertThat(readResponse.getBody()).isNotNull().hasSize(1);
        assertThat(readResponse.getBody().get(0).getDescription()).isEqualTo(createActivityRequest.getDescription());

        // Read by id
        UUID activityDd = readResponse.getBody().get(0).getId();
        ResponseEntity<ActivityResponse> readResponse2 = restTemplate.exchange(
                MappingRegistry.REQUEST_MAPPING_ACTIVITIES + "/{id}",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                ActivityResponse.class,
                activityDd
        );

        assertThat(readResponse2.getStatusCode()).isEqualTo(OK);
        assertThat(readResponse2.getHeaders().getContentType()).isNotNull().isEqualTo(MediaType.APPLICATION_JSON);

        assertThat(readResponse2.getBody()).isNotNull();
        assertThat(readResponse2.getBody().getDescription()).isEqualTo(createActivityRequest.getDescription());


        // Read contact activity statistics
        ResponseEntity<ActivityStatisticResponse> readResponse3 = restTemplate.exchange(
                MappingRegistry.REQUEST_MAPPING_CONTACTS + "/{id}/statistics",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                ActivityStatisticResponse.class,
                createResponseBody.getId()
        );

        assertThat(readResponse3.getStatusCode()).isEqualTo(OK);
        assertThat(readResponse3.getHeaders().getContentType()).isNotNull().isEqualTo(MediaType.APPLICATION_JSON);

        assertThat(readResponse3.getBody()).isNotNull();
        assertThat(readResponse3.getBody().getContactId()).isNotNull().isEqualTo(createResponseBody.getId());
        assertThat(readResponse3.getBody().getSummaryCount()).isEqualTo(1L);
        assertThat(readResponse3.getBody().getContactTypeCallCount()).isEqualTo(1L);
        assertThat(readResponse3.getBody().getContactTypeEmailCount()).isEqualTo(0L);
        assertThat(readResponse3.getBody().getContactTypeMeetingCount()).isEqualTo(0L);


    }


    private CreateUpdateContactRequest createContactRequest(UUID id, String firstName, String lastName, String email, String company) {
        CreateUpdateContactRequest request = new CreateUpdateContactRequest();
        request.setId(id);
        request.setFirstName(firstName);
        request.setLastName(lastName);
        request.setEmail(email);
        request.setCompany(company);

        return request;
    }

    private ActivityRequest createActivityRequest(UUID contactid, ActivityType type, String desc, Instant timestamp) {

        ActivityRequest request = new ActivityRequest();
        request.setContactid(contactid);
        request.setType(type);
        request.setDescription(desc);
        request.setTimestamp(timestamp);
        return request;
    }

}
