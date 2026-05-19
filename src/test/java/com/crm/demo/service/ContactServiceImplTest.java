package com.crm.demo.service;

import com.crm.demo.controller.dto.ContactResponse;
import com.crm.demo.controller.dto.CreateUpdateContactRequest;
import com.crm.demo.controller.exception.ContactAlreadyExistsException;
import com.crm.demo.controller.exception.InvalidRequestException;
import com.crm.demo.model.Contact;
import com.crm.demo.repository.ContactRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ContactServiceImplTest {

    @Mock
    private ContactRepository contactRepo;

    @InjectMocks
    private ContactServiceImpl service;

    @Test
    void getAllActive_returnsResponse(){

        Contact contact1 = Contact.create("Sven1", "Müller1", "sm1@wearethecompany.de", "wearethecompany1");
        Contact contact2 = Contact.create("Sven2", "Müller2", "sm2@wearethecompany.de", "wearethecompany2");

        assertThat(contact1.getId()).isNull();
        assertThat(contact2.getId()).isNull();

        ReflectionTestUtils.setField(contact1, "id", UUID.randomUUID());
        ReflectionTestUtils.setField(contact2, "id", UUID.randomUUID());

        assertThat(contact1.isActive()).isTrue();
        assertThat(contact2.isActive()).isTrue();

        when(contactRepo.findByActive(true)).thenReturn(List.of(contact1, contact2));

        List<ContactResponse> activteContactes =  service.getAll(true);
        assertThat(activteContactes).isNotNull().isNotEmpty().hasSize(2);

        assertThat(activteContactes.get(0).getId()).isEqualTo(contact1.getId());
        assertThat(activteContactes.get(0).getEmail()).isEqualTo(contact1.getEmail());
        assertThat(activteContactes.get(0).getCompany()).isEqualTo(contact1.getCompany());
        assertThat(activteContactes.get(0).getFirstName()).isEqualTo(contact1.getFirstName());
        assertThat(activteContactes.get(0).getLastName()).isEqualTo(contact1.getLastName());
        assertThat(activteContactes.get(0).isActive()).isTrue();

        assertThat(activteContactes.get(1).getId()).isEqualTo(contact2.getId());
        assertThat(activteContactes.get(1).getEmail()).isEqualTo(contact2.getEmail());
        assertThat(activteContactes.get(1).getCompany()).isEqualTo(contact2.getCompany());
        assertThat(activteContactes.get(1).getFirstName()).isEqualTo(contact2.getFirstName());
        assertThat(activteContactes.get(1).getLastName()).isEqualTo(contact2.getLastName());
        assertThat(activteContactes.get(1).isActive()).isTrue();

        when(contactRepo.findByActive(true)).thenReturn(List.of(contact2));
        activteContactes =  service.getAll(true);
        assertThat(activteContactes).isNotNull().isNotEmpty().hasSize(1);
        assertThat(activteContactes.get(0).getId()).isEqualTo(contact2.getId());
    }

    @Test
    void getById_returnsResponse(){

        Contact contact = Contact.create("Sven1", "Müller1", "sm1@wearethecompany.de", "wearethecompany1");
        assertThat(contact.getId()).isNull();

        UUID contactId = UUID.randomUUID();
        ReflectionTestUtils.setField(contact, "id", contactId);

        when(contactRepo.findById(eq(contactId))).thenReturn(Optional.of(contact));

        ContactResponse foundContact = service.getById(contactId);
        assertThat(foundContact).isNotNull();
        assertThat(foundContact.getId()).isEqualTo(contactId);
        assertThat(foundContact.getEmail()).isEqualTo(contact.getEmail());
        assertThat(foundContact.getCompany()).isEqualTo(contact.getCompany());
        assertThat(foundContact.getFirstName()).isEqualTo(contact.getFirstName());
        assertThat(foundContact.getLastName()).isEqualTo(contact.getLastName());

        // any random uuid
        foundContact = service.getById(UUID.randomUUID());
        assertThat(foundContact).isNull();

        verify(contactRepo, times(2)).findById(any());
    }

    @Test
    void create_createsAndReturnsResponse(){

        Contact contact = Contact.create("Sven1", "Müller1", "sm1@wearethecompany.de", "wearethecompany1");
        assertThat(contact.getId()).isNull();

        UUID contactId = UUID.randomUUID();
        ReflectionTestUtils.setField(contact, "id", contactId);

        when(contactRepo.save(any())).thenReturn(contact);

        CreateUpdateContactRequest req = new CreateUpdateContactRequest();
        req.setId(null);
        req.setFirstName(contact.getFirstName());
        req.setLastName(contact.getLastName());
        req.setEmail(contact.getEmail());
        req.setCompany(contact.getCompany());

        ContactResponse createdContact = service.create(new CreateUpdateContactRequest());
        assertThat(createdContact).isNotNull();
        assertThat(createdContact.getId()).isEqualTo(contactId);
        assertThat(createdContact.getEmail()).isEqualTo(contact.getEmail());
        assertThat(createdContact.getCompany()).isEqualTo(contact.getCompany());
        assertThat(createdContact.getFirstName()).isEqualTo(contact.getFirstName());
        assertThat(createdContact.getLastName()).isEqualTo(contact.getLastName());

        verify(contactRepo, times(1)).save(any());
    }

    @Test
    void create_throwsExceptions(){

        CreateUpdateContactRequest req = new CreateUpdateContactRequest();
        req.setId(UUID.randomUUID());
        req.setFirstName("Sven1");
        req.setLastName("Meller1");
        req.setEmail("wearethecompany.de");
        req.setCompany("wearethecompany");

        assertThrows(InvalidRequestException.class, () -> {
            service.create(req);
        });

        when(contactRepo.existsByFirstNameAndLastNameAndEmailAndIdNot(eq(req.getFirstName()), eq(req.getLastName()), eq(req.getEmail()), eq(null))).thenReturn(true);
        req.setId(null);

        assertThrows(ContactAlreadyExistsException.class, () -> {
            service.create(req);
        });

        verify(contactRepo, times(1)).existsByFirstNameAndLastNameAndEmailAndIdNot(any(), any(), any(), any());
    }

    @Test
    void update_updatesAndReturnsResponse(){

        Contact initialContact = Contact.create("Sven1", "Müller1", "sm1@wearethecompany.de", "wearethecompany1");
        Contact updatedContact = Contact.create("Sven2", "Müller2", "sm1@wearethecompany.de", "wearethecompany1");

        UUID contactId = UUID.randomUUID();

        ReflectionTestUtils.setField(initialContact, "id", contactId);
        ReflectionTestUtils.setField(updatedContact, "id", contactId);

        // mocks
        when(contactRepo.findById(eq(contactId))).thenReturn(Optional.of(initialContact));
        when(contactRepo.save(any())).thenReturn(updatedContact);
        when(contactRepo.existsByFirstNameAndLastNameAndEmailAndIdNot(any(), any(), any(), any())).thenReturn(false);

        CreateUpdateContactRequest updateRequest = new  CreateUpdateContactRequest();
        updateRequest.setId(updatedContact.getId());
        updateRequest.setFirstName(updatedContact.getFirstName());
        updateRequest.setLastName(updatedContact.getLastName());
        updateRequest.setEmail(updatedContact.getEmail());
        updateRequest.setCompany(updatedContact.getCompany());

        ContactResponse updateResponse = service.update(updatedContact.getId(), updateRequest);
        assertThat(updateResponse.getId()).isEqualTo(contactId);

        verify(contactRepo, times(1)).findById(eq(contactId));
        verify(contactRepo, times(1)).save(any());
        verify(contactRepo, times(1)).existsByFirstNameAndLastNameAndEmailAndIdNot(any(), any(), any(), any());
    }

    @Test
    void update_throwsExceptions(){

        CreateUpdateContactRequest req = new CreateUpdateContactRequest();
        req.setId(null);
        req.setFirstName("Sven1");
        req.setLastName("Meller1");
        req.setEmail("wearethecompany.de");
        req.setCompany("wearethecompany");

        // kein rquest id
        assertThrows(InvalidRequestException.class, () -> {
            service.update(UUID.randomUUID(), req);
        });

        req.setId(UUID.randomUUID());
        // id in request, aber unetrscheidet sich von anderem parameter
        assertThrows(InvalidRequestException.class, () -> {
            service.update(UUID.randomUUID(), req);
        });

        when(contactRepo.existsByFirstNameAndLastNameAndEmailAndIdNot(eq(req.getFirstName()), eq(req.getLastName()), eq(req.getEmail()), eq(req.getId()))).thenReturn(true);

        assertThrows(ContactAlreadyExistsException.class, () -> {
            service.update(req.getId(), req);
        });

        verify(contactRepo, times(1)).existsByFirstNameAndLastNameAndEmailAndIdNot(eq(req.getFirstName()), eq(req.getLastName()), eq(req.getEmail()), eq(req.getId()));
    }



    @Test
    void deactivate_deactivatesAndReturnsResponse(){

        Contact activeContact = Contact.create("Sven1", "Müller1", "sm1@wearethecompany.de", "wearethecompany1");
        UUID contactId = UUID.randomUUID();
        ReflectionTestUtils.setField(activeContact, "id", contactId);
        assertThat(activeContact.isActive()).isTrue();

        Contact inactiveContact = Contact.create("Sven1", "Müller1", "sm1@wearethecompany.de", "wearethecompany1");
        ReflectionTestUtils.setField(inactiveContact, "id", contactId);
        assertThat(inactiveContact.isActive()).isTrue();
        inactiveContact.setActive(false);

        when(contactRepo.findById(eq(contactId))).thenReturn(Optional.of(activeContact));
        when(contactRepo.save(any())).thenReturn(inactiveContact);

        ContactResponse deactivatedContact = service.deactivate(contactId);

        assertThat(deactivatedContact).isNotNull();
        assertThat(deactivatedContact.getId()).isEqualTo(inactiveContact.getId());
        assertThat(deactivatedContact.getEmail()).isEqualTo(inactiveContact.getEmail());
        assertThat(deactivatedContact.getCompany()).isEqualTo(inactiveContact.getCompany());
        assertThat(deactivatedContact.getFirstName()).isEqualTo(inactiveContact.getFirstName());
        assertThat(deactivatedContact.getLastName()).isEqualTo(inactiveContact.getLastName());

        assertThat(deactivatedContact.isActive()).isFalse();
    }

    @Test
    void allMethodsValidation_throwsNullPointerException(){

        assertThrows(NullPointerException.class, () -> {
            service.getById(null);
        });

        assertThrows(NullPointerException.class, () -> {
            service.create(null);
        });


        assertThrows(NullPointerException.class, () -> {
            service.update(null, new CreateUpdateContactRequest());
        });
        assertThrows(NullPointerException.class, () -> {
            service.update(UUID.randomUUID(), null);
        });

        assertThrows(NullPointerException.class, () -> {
            service.deactivate(null);
        });
    }
}
