package com.cliniqo.publicwebsite.repository;

import com.cliniqo.publicwebsite.entity.PublicWebsiteSlugReservation;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PublicWebsiteSlugReservationRepository extends JpaRepository<PublicWebsiteSlugReservation, UUID> {
    boolean existsBySlug(String slug);
}
