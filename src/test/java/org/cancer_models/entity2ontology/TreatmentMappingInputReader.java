package org.cancer_models.entity2ontology;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TreatmentMappingInputReader {
    public static List<TreatmentMappingInputFileEntry> parseTSV(String filePath) throws IOException {
        List<TreatmentMappingInputFileEntry> entries = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            // Skip the header line if there is one
            br.readLine(); // assuming the first line is the header
            while ((line = br.readLine()) != null) {
                String[] values = line.split("\t");

                TreatmentMappingInputFileEntry entry = new TreatmentMappingInputFileEntry();
                entry.setIndexPath(values[0]);
                entry.setEntryId(values[1]);
                entry.setTreatmentName(values[2]);
                entry.setExpectedLabel(values[3]);
                entry.setMinimumScore(Double.parseDouble(values[4]));

                entries.add(entry);
            }
        }

        return entries;
    }
}
