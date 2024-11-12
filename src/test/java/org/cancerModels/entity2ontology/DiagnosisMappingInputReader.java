package org.cancerModels.entity2ontology;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DiagnosisMappingInputReader {
    public static List<DiagnosisMappingInputFileEntry> parseTSV(String filePath) throws IOException {
        List<DiagnosisMappingInputFileEntry> entries = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            // Skip the header line if there is one
            br.readLine(); // assuming the first line is the header
            while ((line = br.readLine()) != null) {
                System.out.println(line);
                String[] values = line.split("\t");

                DiagnosisMappingInputFileEntry entry = new DiagnosisMappingInputFileEntry();
                entry.setIndexPath(values[0]);
                entry.setEntryId(values[1]);
                entry.setSampleDiagnosis(values[2]);
                entry.setOriginTissue(values[3]);
                entry.setTumorType(values[4]);
                entry.setExpectedLabel(values[5]);
                entry.setMinimumScore(Double.parseDouble(values[6]));

                entries.add(entry);
            }
        }

        return entries;
    }

    public static void main(String[] args) {
        try {
            List<DiagnosisMappingInputFileEntry> entries = parseTSV("path/to/your/file.tsv");
            entries.forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
