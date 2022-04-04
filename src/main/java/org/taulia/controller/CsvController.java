package org.taulia.controller;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.taulia.service.CsvService;

@RestController()
@RequestMapping(value = "/")
@OpenAPIDefinition(info = @Info(title = "Info",
        version = "0.1",
        description = "CSV API",
        contact = @Contact(name = "Elka Ganeva")
))
public class CsvController {

    private final CsvService processor;

    public CsvController(CsvService processor) {
        this.processor = processor;
    }

    @Operation(summary = "CSV to CSVs", description = "Splits a CSV invoices file by buyer", tags = {"CSV"})
    @PostMapping(value = "/csv", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE }, produces = "application/json")
    public ResponseEntity<?> generateCsvFiles(@RequestPart MultipartFile file) throws Throwable {
        try {
            return processor.processCsv(file).get();
        } catch (Exception e) {
            throw e.getCause();
        }
    }

    @Operation(summary = "CSV to XML and invoice images", description = "Converts CSV to XML and saves invoice images", tags = {"XML"})
    @PostMapping(value = "/xml", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE }, produces = "application/json")
    public ResponseEntity<?> generateXml(@RequestPart MultipartFile file) throws Throwable {
        try {
            return processor.generateXml(file).get();
        } catch (Exception e) {
            throw e.getCause();
        }
    }
}
