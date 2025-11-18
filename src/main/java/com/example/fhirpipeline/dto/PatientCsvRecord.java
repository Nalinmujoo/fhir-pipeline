package com.example.fhirpipeline.dto;

/**
 * Represents a single row from the patients.csv file.
 */
public record PatientCsvRecord(
        String id,
        String firstName,
        String lastName,
        String dob,
        String gender
) {
}
