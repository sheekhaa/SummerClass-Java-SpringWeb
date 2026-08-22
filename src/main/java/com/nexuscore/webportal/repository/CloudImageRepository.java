package com.nexuscore.webportal.repository;

import com.nexuscore.webportal.model.CloudImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CloudImageRepository extends JpaRepository<CloudImage, Integer> {
}
