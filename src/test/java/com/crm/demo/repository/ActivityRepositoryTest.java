package com.crm.demo.repository;

import com.crm.demo.model.Activity;
import com.crm.demo.model.ActivityType;
import com.crm.demo.model.Contact;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.domain.Sort;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
        "spring.jpa.hibernate.ddl-auto=create"
})
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ActivityRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withEnv("POSTGRES_INITDB_ARGS", "--encoding=UTF8 --locale=de_DE.UTF-8");

    @Autowired
    ActivityRepository activityRepo;

    @Autowired
    ContactRepository contactRepo;

    @Test
    void save_persists() {

        Contact savedContact = contactRepo.saveAndFlush(
                Contact.create("Sven", "Müller", "sm@wearethecompany.de", "wearethecompany")
        );

        assertThat(savedContact.getId()).isNotNull();

        Activity activity1 = Activity.create(savedContact, ActivityType.MEETING, "Initial meet& greed", Instant.now());
        assertThat(activity1.getId()).isNull();

        Activity savedActivity1 = activityRepo.saveAndFlush(activity1);

        assertThat(savedActivity1).isNotNull();
        assertThat(savedActivity1.getId()).isNotNull();
    }

    @Test
    void delete_byContact() {

        Contact savedContact = contactRepo.saveAndFlush(
                Contact.create("Sven", "Müller", "sm@wearethecompany.de", "wearethecompany")
        );

        assertThat(savedContact.getId()).isNotNull();

        Activity activity1 = Activity.create(savedContact, ActivityType.MEETING, "Initial meet& greed", Instant.now());
        assertThat(activity1.getId()).isNull();

        Activity savedActivity1 = activityRepo.saveAndFlush(activity1);

        assertThat(savedActivity1).isNotNull();
        assertThat(savedActivity1.getId()).isNotNull();
        assertThat(savedActivity1.getContact()).isNotNull();
        assertThat(savedActivity1.getType()).isEqualTo(ActivityType.MEETING);
        assertThat(savedActivity1.getDescription()).isEqualTo(activity1.getDescription());
        assertThat(savedActivity1.getTimestamp()).isBefore(Instant.now());


        assertThat(activityRepo.count()).isEqualTo(1);

        contactRepo.delete(savedContact);
        contactRepo.flush();

        assertThat(activityRepo.count()).isEqualTo(0);
    }

    @Test
    void findByContactId_ok() {
        Contact savedContact = contactRepo.saveAndFlush(
                Contact.create("Sven", "Müller", "sm@wearethecompany.de", "wearethecompany")
        );

        assertThat(savedContact.getId()).isNotNull();

        Activity activity1 = Activity.create(savedContact, ActivityType.MEETING, "Initial meet& greed", Instant.now());
        Activity activity2 = Activity.create(savedContact, ActivityType.CALL, "Dinner call", Instant.now());

        assertThat(activity1.getId()).isNull();
        assertThat(activity2.getId()).isNull();

        List<Activity> savedActivities = activityRepo.saveAllAndFlush(List.of(activity1, activity2));
        assertThat(savedActivities).isNotEmpty();
        assertThat(savedActivities.size()).isEqualTo(2);

        List<Activity> myActivities = activityRepo.findByContactId(savedContact.getId(), Sort.by("timestamp").descending());
        assertThat(myActivities).isNotEmpty();
        assertThat(myActivities.size()).isEqualTo(2);

        assertThat(myActivities.get(0).getTimestamp()).isAfter(myActivities.get(1).getTimestamp());

        myActivities = activityRepo.findByContactId(savedContact.getId(), Sort.by("timestamp").ascending());
        assertThat(myActivities.get(0).getTimestamp()).isBefore(myActivities.get(1).getTimestamp());

    }

    @Test
    void countByContactIdAndType_ok() {
        Contact savedContact = contactRepo.saveAndFlush(
                Contact.create("Sven", "Müller", "sm@wearethecompany.de", "wearethecompany")
        );

        assertThat(savedContact.getId()).isNotNull();

        Activity activity1 = Activity.create(savedContact, ActivityType.MEETING, "Initial meet& greed", Instant.now());
        Activity activity2 = Activity.create(savedContact, ActivityType.CALL, "Dinner call", Instant.now());

        assertThat(activity1.getId()).isNull();
        assertThat(activity2.getId()).isNull();

        List<Activity> savedActivities = activityRepo.saveAllAndFlush(List.of(activity1, activity2));
        assertThat(savedActivities).isNotEmpty();
        assertThat(savedActivities.size()).isEqualTo(2);

        long meetingsCount = activityRepo.countByContactIdAndType(savedContact.getId(), ActivityType.MEETING);
        long callsCount = activityRepo.countByContactIdAndType(savedContact.getId(), ActivityType.CALL);
        long emailsCount = activityRepo.countByContactIdAndType(savedContact.getId(), ActivityType.EMAIL);

        assertThat(meetingsCount).isEqualTo(1L);
        assertThat(callsCount).isEqualTo(1L);
        assertThat(emailsCount).isEqualTo(0L);
    }
}
