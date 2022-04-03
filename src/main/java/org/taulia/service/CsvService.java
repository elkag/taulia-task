package org.taulia.service;

import com.opencsv.exceptions.CsvValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.taulia.model.Response;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Future;

@Service
public class CsvService {

    @Value("${app.batch.size}")
    private Integer batchSize;

    @Value("${app.column.separator}")
    private String columnSeparator;

    private final Logger logger = LoggerFactory.getLogger(CsvService.class);

    @Async("processCsvTaskExecutor")
    public Future<ResponseEntity<Response>> processCsv(MultipartFile file) throws CsvValidationException, IOException {

        Path path = Paths.get(System.getProperty("user.home") + "\\taulia\\temp_" + System.currentTimeMillis());
        Processor processor = new CsvFilesGenerator(batchSize, columnSeparator);

        logger.info(String.format("Start processing file: %s", file.getOriginalFilename()));
        try {
            Response response = processor.initialize(file, path);
            return new AsyncResult<>(ResponseEntity.ok().body(response));
        } catch (Exception e){
            processor.removeCreatedFiles(path);
            throw e;
        }
    }

    @Async("processCsvTaskExecutor")
    public Future<ResponseEntity<Response>> generateXml(MultipartFile file) throws IOException, CsvValidationException {

        Path path = Paths.get(System.getProperty("user.home") + "\\taulia\\temp_" + System.currentTimeMillis());
        Processor processor = new XMLFileGenerator();

        try {
            Response response = processor.initialize(file, path);
            return new AsyncResult<>(ResponseEntity.ok().body(response));
        } catch (Exception e){
            processor.removeCreatedFiles(path);
            throw e;
        }
    }
}
