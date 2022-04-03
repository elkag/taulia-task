package org.taulia.exception;

import com.opencsv.exceptions.CsvValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.ResourceAccessException;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ExecutionException;

@ControllerAdvice
public class AppExceptionHandler {


    @ExceptionHandler({ NullPointerException.class, ExecutionException.class, InterruptedException.class, IOException.class})
    public ResponseEntity<ErrorMessage> nullExceptionHandler(Exception ex) {
        ErrorMessage message = new ErrorMessage(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                new Date(),
                ex.getMessage());

        return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(CsvValidationException.class)
    public ResponseEntity<ErrorMessage> handleValidationExceptions(CsvValidationException ex) {
        ErrorMessage message = new ErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                new Date(),
                ex.getMessage());

        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }
}
