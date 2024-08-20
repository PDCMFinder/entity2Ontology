package org.cancerModels.entity2ontology;

import lombok.Data;

// A utility class to capture an entry from a tsv file to test the mapping process
// for diagnosis
@Data
public class DiagnosisMappingInputFileEntry {
    private String indexPath;
    private String entryId;
    private String sampleDiagnosis;
    private String originTissue;
    private String tumorType;
    private String expectedLabel;
}
