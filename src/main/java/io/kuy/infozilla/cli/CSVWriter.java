package io.kuy.infozilla.cli;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class CSVWriter {
    public static void run(String cleaned, String patches, String traces, String regions, String enumerations) throws IOException {
        try (
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out));

                CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                        .withHeader("cleaned", "patches", "traces", "regions", "enumerations"));
        ) {
            csvPrinter.printRecord(cleaned, patches, traces, regions, enumerations);
            csvPrinter.flush();
        }
    }
}
