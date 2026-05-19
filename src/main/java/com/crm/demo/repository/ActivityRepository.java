package com.crm.demo.repository;

import com.crm.demo.model.Activity;
import com.crm.demo.model.ActivityType;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, UUID> {

    List<Activity> findByContactId(UUID id, Sort sort);

    long countByContactIdAndType(UUID id, ActivityType type);
}
