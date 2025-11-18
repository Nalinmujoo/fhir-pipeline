package com.example.fhirpipeline.service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import com.example.fhirpipeline.dto.PatientCsvRecord;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.InputStreamReader;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class FhirTransformationService {

    // thread-safe and can be created once and reused.
    private final FhirContext fhirContext = FhirContext.forR4();
    private final IParser jsonParser = fhirContext.newJsonParser().setPrettyPrint(true);

    /**
     * Main method to process the CSV file.
     * It reads the file, transforms each row to a Patient, and returns a list of JSON strings.
     */
    public List<String> transformCsvToFhirJson() throws Exception {
        List<String> fhirJsonList = new ArrayList<>();

        Resource csvResource = new ClassPathResource("patients.csv");

        try (
                Reader reader = new InputStreamReader(csvResource.getInputStream());
                CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.builder()
                        .setHeader()
                        .setSkipHeaderRecord(true)
                        .setTrim(true)
                        .build())
        ) {

            for (CSVRecord csvRecord : csvParser) {
                // map CSV row to the DTO
                PatientCsvRecord patientDto = new PatientCsvRecord(
                        csvRecord.get("id"),
                        csvRecord.get("first_name"),
                        csvRecord.get("last_name"),
                        csvRecord.get("dob"),
                        csvRecord.get("gender")
                );

                Patient fhirPatient = mapDtoToFhirPatient(patientDto);

                String fhirJsonString = jsonParser.encodeResourceToString(fhirPatient);
                fhirJsonList.add(fhirJsonString);
            }
        }

        return fhirJsonList;
    }

    /**
     * Helper method to map the simple DTO to a HAPI FHIR Patient resource.
     */
    private Patient mapDtoToFhirPatient(PatientCsvRecord dto) throws Exception {
        Patient patient = new Patient();

        patient.setId(dto.id());

        HumanName name = patient.addName();
        name.setFamily(dto.lastName());
        name.addGiven(dto.firstName());
        name.setUse(HumanName.NameUse.OFFICIAL);

        if ("male".equalsIgnoreCase(dto.gender())) {
            patient.setGender(Enumerations.AdministrativeGender.MALE);
        } else if ("female".equalsIgnoreCase(dto.gender())) {
            patient.setGender(Enumerations.AdministrativeGender.FEMALE);
        } else {
            patient.setGender(Enumerations.AdministrativeGender.UNKNOWN);
        }

        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        patient.setBirthDate(f.parse(dto.dob()));

        return patient;
    }
}
