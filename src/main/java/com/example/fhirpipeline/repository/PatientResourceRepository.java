package com.example.fhirpipeline.repository;

import com.example.fhirpipeline.entity.PatientResourceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientResourceRepository extends JpaRepository<PatientResourceEntity, String> {
}
