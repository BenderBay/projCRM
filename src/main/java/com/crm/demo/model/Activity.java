package com.crm.demo.model;

/**
 * Domain model for tasks
 */

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;
import java.util.UUID;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "activity")
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "UUID", nullable = false)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "contact_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @Setter
    Contact contact;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Setter
    ActivityType type;

    @Column(nullable = true, unique = false, length = 150)
    @Setter
    String description;

    @Column(nullable = false)
    Instant timestamp;

    public static Activity create(Contact contact, ActivityType type, String description, Instant timestamp) {
        Activity next = new Activity();
        next.contact = contact;
        next.type = type;
        next.description = description;
        next.timestamp = timestamp;

        return next;
    }
}
