package org.taulia.service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import org.taulia.batch.BatchProcessor;
import org.taulia.model.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.*;

public class CsvFilesGenerator extends Processor {

    private final BatchProcessor processor;

    private final Integer batchSize;
    private String[] header;

    private final Logger logger = LoggerFactory.getLogger(CsvFilesGenerator.class);

    public CsvFilesGenerator(Integer batchSize, String columnSeparator) {
        this.batchSize = batchSize;
        this.processor = new BatchProcessor(columnSeparator);
    }

    public Response execute(MultipartFile file, Path path) throws IOException, CsvValidationException {
        ArrayList<String[]> lines = new ArrayList<>();
        InputStream inputStream = file.getInputStream();
        logger.info(Thread.currentThread() + "Start reading file: " + file.getOriginalFilename());
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            CSVReader csvReader = new CSVReader(reader);
            String [] line;

            this.header = validate(csvReader.readNext());
            while ((line = csvReader.readNext()) != null) {
                validate(line);
                lines.add(line);

                if(lines.size() >= batchSize) {

                    HashMap<String, List<String[]>> toProcess = getAsMap(path, lines);
                    lines = new ArrayList<>();

                    processor.process(toProcess);
                }
            }
            HashMap<String, List<String[]>> toProcess = getAsMap(path, lines);
            processor.process(toProcess);
            logger.info("End reading file: " + file.getOriginalFilename());
        }
        return getResponse();
    }

    private HashMap<String, List<String[]>> getAsMap(Path path, ArrayList<String[]> lines) {
        HashMap<String, List<String[]>> toProcess = new LinkedHashMap<>();
        for (String[] str : lines) {
            String fileName = path + "\\" + str[0].replaceAll("[^a-zA-Z0-9-_\\.]", "_") + ".csv";
            toProcess.computeIfAbsent(fileName, k -> new ArrayList<>());
            if(!fileNames.contains(fileName)) {
                toProcess.get(fileName).add(header);
            }
            toProcess.get(fileName).add(str);
            fileNames.add(fileName);
        }
        return toProcess;
    }
}
