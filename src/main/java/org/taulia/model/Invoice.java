package org.taulia.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter @Setter
@JacksonXmlRootElement(localName = "invoice")
public class Invoice implements Serializable {

    private String buyer;

    @JsonProperty("image_name")
    private String imageName;

    @JsonProperty("due_date")
    private String dueDate;

    private String number;
    private String amount;
    private String currency;
    private String status;
    private String supplier;
}
