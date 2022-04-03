package org.taulia.service;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import org.taulia.model.CsvRowMapper;
import org.taulia.model.Invoice;
import org.taulia.model.Response;
import org.taulia.util.FileUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;

public class XMLFileGenerator extends Processor {

    private final Logger logger = LoggerFactory.getLogger(CsvService.class);

    ExecutorService executor = Executors.newFixedThreadPool(5);
    Set<Future<Boolean>> savePngFutures = new LinkedHashSet<>();

    @Override
    protected Response execute(MultipartFile file, Path path) throws IOException, CsvValidationException {
        List<Invoice> invoices = new ArrayList<>();
        Set<String> fileNames = new LinkedHashSet<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            CSVReader csvReader = new CSVReader(reader);
            String[] line;
            // Skip header
            String[] header = csvReader.readNext();
            if(header.length != 9) {
                throw new CsvValidationException("Invalid csv file.");
            }
            // Read all lines
            while ((line = csvReader.readNext()) != null) {
                String image = line[2];
                if(image != null && !image.isEmpty() && image.matches("^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)?$")) {
                    // decode the image base64 string and write to file
                    byte[] decodedBytes = Base64
                            .getDecoder()
                            .decode(image);
                    savePngFile(new File(path + "\\" + line[1]), decodedBytes);
                    fileNames.add(path + "\\" + line[1]);
                }
                invoices.add(CsvRowMapper.map(line));
            }
        }

        for (Future<Boolean> future : savePngFutures) {
            boolean success = false;
            try {
                success = future.get(20, TimeUnit.SECONDS);

            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                logger.error(e.getMessage());
            }
            if(!success) throw new IOException("Can not write the file.");
        }

        // Map to xml
        XmlMapper xmlMapper = new XmlMapper();
        String content = xmlMapper.writerWithDefaultPrettyPrinter().writeValueAsString(invoices);
        // Write into xml file
        FileUtil.write(path + "\\invoices.xml", content);
        fileNames.add(path + "\\invoices.xml");
        return getResponse(fileNames);
    }

    private void savePngFile(File file, byte[] bytes) {
        savePngFutures.add(executor.submit(() -> {
            try {
                FileUtils.writeByteArrayToFile(file, bytes);
                return true;
            } catch (IOException e) {
                logger.error(e.getMessage());
                return false;
            }
        }));
    }

    private Response getResponse(Set<String> fileNames) {
        Response response = new Response();
        response.setSuccess(true);
        response.setCreatedFiles(fileNames);
        return response;
    }
}
