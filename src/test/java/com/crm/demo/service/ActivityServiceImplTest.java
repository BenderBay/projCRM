package com.crm.demo.service;

import com.crm.demo.controller.dto.ActivityRequest;
import com.crm.demo.controller.dto.ActivityResponse;
import com.crm.demo.controller.dto.ActivityStatisticResponse;
import com.crm.demo.controller.exception.ResourceNotFoundException;
import com.crm.demo.model.Activity;
import com.crm.demo.model.ActivityType;
import com.crm.demo.model.Contact;
import com.crm.demo.repository.ActivityRepository;
import com.crm.demo.repository.ContactRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ActivityServiceImplTest {

    @Mock
    private ActivityRepository activityRepo;

    @Mock
    private ContactRepository contactRepo;

    @InjectMocks
    private ActivityServiceImpl service;

    @Test
    void create_savesTaskAndReturnsResponse() {

        Contact contact = Contact.create("Sven", "Müller", "sm@wearethecompany.de", "wearethecompany");
        assertThat(contact.getId()).isNull();
        ReflectionTestUtils.setField(contact, "id", UUID.randomUUID());

        Activity activity = Activity.create(contact, ActivityType.CALL, "Initial call", Instant.now());

        when(contactRepo.findById(any())).thenReturn(Optional.of(contact));
        when(activityRepo.save(any(Activity.class))).thenReturn(activity);


        ActivityRequest req = createActivityRequest();
        req.setContactid(contact.getId());

        ActivityResponse response = service.create(contact.getId(), req);

        assertThat(response).isNotNull();
        assertThat(response.getType()).isEqualTo(activity.getType());
        assertThat(response.getDescription()).isEqualTo(activity.getDescription());

        verify(contactRepo, times(1)).findById(any());
        verify(activityRepo, times(1)).save(any(Activity.class));
    }


    @Test
    void getByContactId_returnsResponse(){

        Contact contact = Contact.create("Sven", "Müller", "sm@wearethecompany.de", "wearethecompany");
        assertThat(contact.getId()).isNull();
        UUID contactId = UUID.randomUUID();

        Activity activity1 = Activity.create(contact, ActivityType.CALL, "Initial call", Instant.now());
        Activity activity2 = Activity.create(contact, ActivityType.EMAIL, "Email after initial call", Instant.now());

        when(contactRepo.findById(any())).thenReturn(Optional.of(contact));
        when(activityRepo.findByContactId(any(), any())).thenReturn(List.of(activity2, activity1));

        List<ActivityResponse> activities =  service.getByContactId(contactId);
        assertThat(activities).isNotNull();
        assertThat(activities).isNotEmpty();
        assertThat(activities.size()).isEqualTo(2);

    }


    @Test
    void getById_returnsResponse(){
        Contact contact = Contact.create("Sven", "Müller", "sm@wearethecompany.de", "wearethecompany");
        assertThat(contact.getId()).isNull();

        Activity activity1 = Activity.create(contact, ActivityType.CALL, "Initial call", Instant.now());
        UUID activityId = UUID.randomUUID();

        when(activityRepo.findById(activityId)).thenReturn(Optional.of(activity1));

        ActivityResponse foundActivity = service.getById(activityId);
        assertThat(foundActivity).isNotNull();
        assertThat(foundActivity.getType()).isEqualTo(activity1.getType());
        assertThat(foundActivity.getDescription()).isEqualTo(activity1.getDescription());
        assertThat(foundActivity.getTimestamp()).isNotNull();

        verify(activityRepo, times(1)).findById(activityId);

        ActivityResponse notFoundActivity = service.getById(UUID.randomUUID());
        assertThat(notFoundActivity).isNull();

    }

    @Test
    void getContactStatisticForContactId_returnsResponse(){

        Contact contact = Contact.create("Sven", "Müller", "sm@wearethecompany.de", "wearethecompany");
        assertThat(contact.getId()).isNull();
        UUID contactId = UUID.randomUUID();

        when(contactRepo.findById(any())).thenReturn(Optional.of(contact));

        when(activityRepo.countByContactIdAndType(any(), eq(ActivityType.CALL))).thenReturn(1L);
        when(activityRepo.countByContactIdAndType(any(), eq(ActivityType.EMAIL))).thenReturn(2L);
        when(activityRepo.countByContactIdAndType(any(), eq(ActivityType.MEETING))).thenReturn(3L);

        ActivityStatisticResponse response = service.getContactStatisticForContactId(contactId);
        assertThat(response).isNotNull();
        assertThat(response.getContactTypeCallCount()).isEqualTo(1L);
        assertThat(response.getContactTypeEmailCount()).isEqualTo(2L);
        assertThat(response.getContactTypeMeetingCount()).isEqualTo(3L);

        verify(activityRepo, times(3)).countByContactIdAndType(any(), any());
    }

    @Test
    void allMethodCalls_withUnknownContactID_throwsResourceNotFoundException(){

        assertThrows(ResourceNotFoundException.class, () -> {
            service.getContactStatisticForContactId(UUID.randomUUID());
        });

        assertThrows(ResourceNotFoundException.class, () -> {
            ActivityRequest req = createActivityRequest();
            service.create(req.getContactid(), req);
        });

        assertThrows(ResourceNotFoundException.class, () -> {
            service.getByContactId(UUID.randomUUID());
        });
    }

    @Test
    void allMethodsValidation_throwsNullPointerException(){

        assertThrows(NullPointerException.class, () -> {
            service.getById(null);
        });

        assertThrows(NullPointerException.class, () -> {
            service.getByContactId(null);
        });


        assertThrows(NullPointerException.class, () -> {
            service.create(null, createActivityRequest());
        });
        assertThrows(NullPointerException.class, () -> {
            service.create(UUID.randomUUID(), null);
        });

        assertThrows(NullPointerException.class, () -> {
            service.getContactStatisticForContactId(null);
        });
    }


    public static ActivityRequest createActivityRequest(){

            ActivityRequest req = new ActivityRequest();
            req.setContactid(UUID.randomUUID());
            req.setType(ActivityType.CALL);
            req.setDescription("Initial call");
            req.setTimestamp(Instant.now());

            return req;
    }
}
