/* Slightly modified version of InfoZilla *
 * Credit : InfoZilla Tool
 * <a href='http://groups.csail.mit.edu/pag/pubs/bettenburg-msr-2008.pdf'>
 * Extracting Structural Information From Bug Reports
 * </a>
 * @authors Nicolas Bettenburg, Rahul Premraj, Thomas Zimmermann, Sunghun Kim
 */

package io.kuy.infozilla.cli;

import java.io.File;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.util.Date;

import io.kuy.infozilla.filters.FilterChainEclipse;
import io.kuy.infozilla.helpers.DataExportUtility;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * Main
 */
@Command(name = "infozilla", version = "1.0")
public class Main implements Runnable {

    @Option(names = {"-s", "--with-stacktraces"}, description = "Process and extract stacktraces (default=true)")
    private boolean withStackTraces = true;

    @Option(names = {"-p", "--with-patches"}, description = "Process and extract patches (default=true)")
    private boolean withPatches = true;

    @Option(names = {"-c", "--with-source-code"}, description = "Process and extract source code regions (default=true)")
    private boolean withCode = true;

    @Option(names = {"-l", "--with-lists"}, description = "Process and extract lists (default=true)")
    private boolean withLists = true;

    @Option(names = "--charset", description = "Character Set of Input (default=ISO-8859-1)")
    private String inputCharset = "ISO-8859-1";

    @Parameters(arity = "1..*", paramLabel = "FILE", description = "File(s) to process.")
    private File[] inputFiles;


    @Override
    public void run() {

        for (File f : inputFiles) {
            try {
                process(f);
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        }

    }

    private void process(File f) throws Exception {
        // Read file
        String data = new String(Files.readAllBytes(f.toPath()));

        // Run infozilla
        FilterChainEclipse infozilla_filters = new FilterChainEclipse(data, withPatches, withStackTraces, withCode, withLists);

        String filtered_text = infozilla_filters.getOutputText();

        CSVWriter writer = new CSVWriter();
        writer.run(filtered_text,
                DataExportUtility.getExportOfPatches(infozilla_filters.getPatches(), true),
                DataExportUtility.getExportOfStackTraces(infozilla_filters.getTraces(), true, new Timestamp(new Date().getTime())),
                DataExportUtility.getExportOfSourceCode(infozilla_filters.getRegions(), true),
                DataExportUtility.getExportOfEnumerations(infozilla_filters.getEnumerations(), true)
        );
    }

    public static void main(String[] args) {
        CommandLine.run(new Main(), args);
    }

}