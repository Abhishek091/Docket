package com.parshva.docket.fileReader;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class CSVReader {
    public static List<String> readCsvData(String filePath) {
        List<String> csvData = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                csvData.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the error, e.g., log it or return an empty list
        }
        return csvData;
    }
}
