package org.taulia.service;

import com.opencsv.exceptions.CsvValidationException;
import org.springframework.web.multipart.MultipartFile;
import org.taulia.model.Response;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public abstract class Processor {

    protected final Set<String> fileNames = new HashSet<>();

    public Response initialize(MultipartFile file, Path path) throws IOException, CsvValidationException {
        //noinspection ResultOfMethodCallIgnored
        path.toFile().mkdirs();
        return execute(file, path);
    }

    protected abstract Response execute(MultipartFile file, Path path) throws IOException, CsvValidationException;


    protected String[] validate(String[] line) throws CsvValidationException {
        if(line.length != 9) {
            throw new CsvValidationException("Invalid csv file.");
        }
        return line;
    }

    protected Response getResponse() {
        Response response = new Response();
        response.setSuccess(true);
        response.setCreatedFiles(fileNames);
        return response;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void removeCreatedFiles(Path path) {
        File directory = path.toFile();
        File[] allContents = directory.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                file.delete();
            }
        }
        directory.delete();
    }
}


