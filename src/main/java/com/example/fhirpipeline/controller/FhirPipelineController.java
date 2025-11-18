package com.example.fhirpipeline.controller;

import com.example.fhirpipeline.entity.PatientResourceEntity;
import com.example.fhirpipeline.repository.PatientResourceRepository;
import com.example.fhirpipeline.service.FhirTransformationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class FhirPipelineController {

    private final FhirTransformationService transformationService;
    private final PatientResourceRepository repository;

    public FhirPipelineController(FhirTransformationService transformationService, PatientResourceRepository repository) {
        this.transformationService = transformationService;
        this.repository = repository;
    }

    /**
     * This endpoint triggers the entire CSV-to-FHIR process.
     * URL: POST /api/process
     */
    @PostMapping("/process")
    public ResponseEntity<String> processCsv() {
        try {
            List<String> fhirJsonList = transformationService.transformCsvToFhirJson();

            for (String fhirJson : fhirJsonList) {
                // hacky way to get the ID from the JSON string for the key
                String id = fhirJson.split("\"id\": \"")[1].split("\"")[0];

                PatientResourceEntity entity = new PatientResourceEntity(id, fhirJson);
                repository.save(entity);
            }

            return ResponseEntity.ok("Successfully processed and saved " + fhirJsonList.size() + " patient resources.");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error processing file: " + e.getMessage());
        }
    }

    /**
     * This endpoint retrieves a single patient's FHIR JSON from the database.
     * URL: GET /api/patient/1
     */
    @GetMapping("/patient/{id}")
    public ResponseEntity<String> getPatientById(@PathVariable String id) {
        Optional<PatientResourceEntity> entity = repository.findById(id);

        if (entity.isPresent()) {
            return ResponseEntity.ok(entity.get().getFhirResourceJson());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}