package com.cliniqo.auth.repository;

import com.cliniqo.auth.entity.DefaultPermissionAssignment;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DefaultPermissionAssignmentRepository extends JpaRepository<DefaultPermissionAssignment, UUID> {
}
