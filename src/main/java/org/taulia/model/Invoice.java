package org.taulia.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.opencsv.bean.CsvBindByPosition;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter @Setter
@JacksonXmlRootElement(localName = "invoice")
public class Invoice implements Serializable {
    @CsvBindByPosition(position = 0)
    private String buyer;
    @CsvBindByPosition(position = 1)
    private String imageName;
    @CsvBindByPosition(position = 2)
    private String dueDate;
    @CsvBindByPosition(position = 4)
    private String number;
    @CsvBindByPosition(position = 5)
    private String amount;
    @CsvBindByPosition(position = 6)
    private String currency;
    @CsvBindByPosition(position = 7)
    private String status;
    @CsvBindByPosition(position = 8)
    private String supplier;
}
