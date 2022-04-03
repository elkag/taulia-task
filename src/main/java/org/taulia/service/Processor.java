package org.taulia.service;

import com.opencsv.exceptions.CsvValidationException;
import org.springframework.web.multipart.MultipartFile;
import org.taulia.model.Response;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public abstract class Processor {

    public Response initialize(MultipartFile file, Path path) throws IOException, CsvValidationException {
        //noinspection ResultOfMethodCallIgnored
        path.toFile().mkdirs();
        return execute(file, path);
    }

    protected abstract Response execute(MultipartFile file, Path path) throws IOException, CsvValidationException;

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


