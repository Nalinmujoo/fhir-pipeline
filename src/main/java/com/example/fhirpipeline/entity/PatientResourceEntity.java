package com.example.fhirpipeline.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "patient_resources")
public class PatientResourceEntity {

    @Id
    private String resourceId;

    @Column(columnDefinition = "TEXT")
    private String fhirResourceJson;

    public PatientResourceEntity() {
    }

    public PatientResourceEntity(String resourceId, String fhirResourceJson) {
        this.resourceId = resourceId;
        this.fhirResourceJson = fhirResourceJson;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getFhirResourceJson() {
        return fhirResourceJson;
    }

    public void setFhirResourceJson(String fhirResourceJson) {
        this.fhirResourceJson = fhirResourceJson;
    }
}