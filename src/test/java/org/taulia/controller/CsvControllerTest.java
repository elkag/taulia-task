package org.taulia.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.taulia.Application;
import org.taulia.exception.ErrorMessage;
import org.taulia.model.Response;
import org.taulia.service.CsvService;

import java.io.File;
import java.io.FileInputStream;
import java.util.Objects;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
class CsvControllerTest {

    @Autowired
    CsvController controller;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    CsvService service;

    @Test
    void testGenerateCsvFilesSuccess() throws Exception {

        ClassLoader classLoader = getClass().getClassLoader();
        String fileName = Objects.requireNonNull(classLoader.getResource("invoices_small_list_to_csv.csv")).getFile();
        MockMultipartFile file = new MockMultipartFile("file",new FileInputStream(fileName));

        String result = mockMvc.perform(multipart("/csv").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.files", hasSize(3)))
                .andExpect(jsonPath("$.success", is(true)))
                .andReturn()
                .getResponse().getContentAsString();
        Response response = new ObjectMapper().readValue(result, Response.class);
        response.getCreatedFiles().forEach(f -> Assertions.assertTrue(new File(f).exists()));
        removeCreatedFiles(response);
    }

    @Test
    void generateXmlFileSuccess() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        String fileName = Objects.requireNonNull(classLoader.getResource("invoices_small_list_to_xml.csv")).getFile();
        MockMultipartFile file = new MockMultipartFile("file",new FileInputStream(fileName));

        String result = mockMvc.perform(multipart("/xml").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.files", hasSize(8)))
                .andExpect(jsonPath("$.success", is(true)))
                .andReturn()
                .getResponse().getContentAsString();
        Response response = new ObjectMapper().readValue(result, Response.class);
        response.getCreatedFiles().forEach(f -> Assertions.assertTrue(new File(f).exists()));
        removeCreatedFiles(response);
    }

    @Test
    void testGenerateCsvFilesFailure() throws Exception {

        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                "invalid.csv",
                MediaType.TEXT_PLAIN_VALUE,
                "broken,file".getBytes()
        );

        String result = mockMvc.perform(multipart("/csv").file(file))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse().getContentAsString();
        ErrorMessage error = new ObjectMapper().readValue(result, ErrorMessage.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), error.getStatusCode());
        Assertions.assertEquals("Invalid csv file.", error.getMessage());
    }

    @Test
    void testGenerateXmlFilesFailure() throws Exception {
        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                "invalid.csv",
                MediaType.TEXT_PLAIN_VALUE,
                "broken,file".getBytes()
        );

        String result = mockMvc.perform(multipart("/xml").file(file))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse().getContentAsString();
        ErrorMessage error = new ObjectMapper().readValue(result, ErrorMessage.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), error.getStatusCode());
        Assertions.assertEquals("Invalid csv file.", error.getMessage());
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void removeCreatedFiles(Response response) {
        if(response.getCreatedFiles().size() > 0) {
            File parentDirectory = new File(response.getCreatedFiles().iterator().next()).getParentFile();
            File[] allContents = parentDirectory.listFiles();
            if (allContents != null) {
                for (File file : allContents) {
                    file.delete();
                }
            }
            parentDirectory.delete();
        }
    }
}