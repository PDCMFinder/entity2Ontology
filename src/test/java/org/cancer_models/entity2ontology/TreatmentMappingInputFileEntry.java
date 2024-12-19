package org.cancer_models.entity2ontology;

import lombok.Data;

// A utility class to capture an entry from a tsv file to test the mapping process
// for treatments
@Data
public class TreatmentMappingInputFileEntry {
    private String indexPath;
    private String entryId;
    private String treatmentName;
    private String expectedLabel;
    private double minimumScore;
}
