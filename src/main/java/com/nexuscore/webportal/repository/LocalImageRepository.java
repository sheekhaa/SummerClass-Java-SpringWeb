package com.nexuscore.webportal.repository;

import com.nexuscore.webportal.model.LocalImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocalImageRepository extends JpaRepository<LocalImage, Integer> {
}
