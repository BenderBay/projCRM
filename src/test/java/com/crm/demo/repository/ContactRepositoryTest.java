package com.crm.demo.repository;

import com.crm.demo.model.Contact;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.dao.DataIntegrityViolationException;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DataJpaTest(properties = {
        "spring.jpa.hibernate.ddl-auto=create"
})
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ContactRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withEnv("POSTGRES_INITDB_ARGS", "--encoding=UTF8 --locale=de_DE.UTF-8");

    @Autowired
    private ContactRepository repo;

    @Test
    void save_persists() {
        Contact contact = Contact.create("Sven", "Müller", "sm@wearethecompany.de", "wearethecompany");
        assertThat(contact.getId()).isNull();

        Contact saved = repo.saveAndFlush(contact);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getFirstName()).isEqualTo(contact.getFirstName());
        assertThat(saved.getLastName()).isEqualTo(contact.getLastName());
        assertThat(saved.getEmail()).isEqualTo(contact.getEmail());
        assertThat(saved.getCompany()).isEqualTo(contact.getCompany());
    }

    @Test
    void save_firstLastNameEmailMultiple_throwsDataIntegrityViolationException() throws Exception {
        Contact contact1 = Contact.create("Sven", "Müller", "sm@wearethecompany.de", "wearethecompany");
        Contact contact2 = Contact.create("Sven", "Müller", "sm@wearethecompany.de", "wearethecompany2");

        Contact saved1 = repo.saveAndFlush(contact1);
        assertThat(saved1.getId()).isNotNull();

        assertThrows(DataIntegrityViolationException.class, () -> {
            repo.saveAndFlush(contact2);
        });
    }

    @Test
    void save_firstNameIsNull_throwsDataIntegrityViolationException() {

        assertThrows(DataIntegrityViolationException.class, () -> {
            repo.saveAndFlush(Contact.create(null, "Müller", "sm@wearethecompany.de", "wearethecompany"));
        });

    }

    @Test
    void save_firstNameIsTooLong_throwsDataIntegrityViolationException() {
        assertThrows(DataIntegrityViolationException.class, () -> {
            repo.saveAndFlush(Contact.create("a".repeat(256), "Müller", "sm@wearethecompany.de", "wearethecompany"));
        });
    }


    @Test
    void save_lastNameIsNull_throwsDataIntegrityViolationException() {

        assertThrows(DataIntegrityViolationException.class, () -> {
            repo.saveAndFlush(Contact.create("Sven", null, "sm@wearethecompany.de", "wearethecompany"));
        });

    }

    @Test
    void save_lastNameIsTooLong_throwsDataIntegrityViolationException() {
        assertThrows(DataIntegrityViolationException.class, () -> {
            repo.saveAndFlush(Contact.create("Sven", "a".repeat(256), "sm@wearethecompany.de", "wearethecompany"));
        });
    }

    @Test
    void save_emailIsNull_throwsDataIntegrityViolationException() {

        assertThrows(DataIntegrityViolationException.class, () -> {
            repo.saveAndFlush(Contact.create("Sven", "Müller", null, "wearethecompany"));
        });

    }

    @Test
    void save_emailIsTooLong_throwsDataIntegrityViolationException() {
        assertThrows(DataIntegrityViolationException.class, () -> {
            repo.saveAndFlush(Contact.create("Sven", "Müller", "a".repeat(256), "wearethecompany"));
        });
    }

    @Test
    void save_companyIsNull_persist() {

         assertDoesNotThrow(() -> {
                 repo.saveAndFlush(Contact.create("Sven", "Müller", "sm@wearethecompany.de", null));
         });
    }

    @Test
    void save_companyIsTooLong_throwsDataIntegrityViolationException() {
        assertThrows(DataIntegrityViolationException.class, () -> {
            repo.saveAndFlush(Contact.create("Sven", "Müller", "sm@wearethecompany.de", "a".repeat(256)));
        });
    }

    @Test
    void findByActive_returnsContacts() {

        Contact contact1 = Contact.create("Sven1", "Müller1", "sm1@wearethecompany.de", "wearethecompany1");
        Contact contact2 = Contact.create("Sven2", "Müller2", "sm2@wearethecompany.de", "wearethecompany2");
        contact2.setActive(false);

        assertThat(contact1.isActive()).isTrue();
        assertThat(contact2.isActive()).isFalse();

        List<Contact> saved = repo.saveAllAndFlush(List.of(contact1, contact2));
        assertThat(saved).allMatch(contact -> contact.getId() != null);

        List<Contact> fouundContacts = repo.findByActive(true);
        assertThat(fouundContacts).hasSize(1);
        assertThat(fouundContacts.get(0).getId()).isEqualTo(saved.get(0).getId());

        fouundContacts = repo.findByActive(false);
        assertThat(fouundContacts).hasSize(1);
        assertThat(fouundContacts.get(0).getId()).isEqualTo(saved.get(1).getId());
    }

    @Test
    void existsByFirstNameAndLastNameAndEmailAndIdNot_OK(){

        Contact contact1 = Contact.create("Sven1", "Müller1", "sm1@wearethecompany.de", "wearethecompany1");
        Contact contact2 = Contact.create("Sven2", "Müller2", "sm2@wearethecompany.de", "wearethecompany2");

        List<Contact> saved = repo.saveAllAndFlush(List.of(contact1, contact2));
        assertThat(saved).allMatch(contact -> contact.getId() != null);

        Contact savedContact1 = saved.get(0);
        Contact savedContact2 = saved.get(1);

        // for update validation; trying to update existing contact with combination of firstname, lastname and email of existing another
        assertThat(
                repo.existsByFirstNameAndLastNameAndEmailAndIdNot(savedContact1.getFirstName(), savedContact1.getLastName(), savedContact1.getEmail(), savedContact1.getId())
        ).isFalse();

        assertThat(
                repo.existsByFirstNameAndLastNameAndEmailAndIdNot(savedContact2.getFirstName(), savedContact2.getLastName(), savedContact2.getEmail(), savedContact1.getId())
        ).isTrue();

        // for creation validation
        assertThat(
                repo.existsByFirstNameAndLastNameAndEmailAndIdNot(savedContact1.getFirstName(), savedContact1.getLastName(), savedContact1.getEmail(), null)
        ).isTrue();

        assertThat(
                repo.existsByFirstNameAndLastNameAndEmailAndIdNot("Thomas", "Müller", "tm@fcbayern.de", null)
        ).isFalse();
    }

}
