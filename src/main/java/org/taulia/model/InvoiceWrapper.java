package org.taulia.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@JacksonXmlRootElement(localName = "invoices")
public class InvoiceWrapper implements Serializable {

    @JacksonXmlProperty(localName = "invoice")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<Invoice> invoices = new ArrayList<>();

    public List<Invoice> getInvoices() {
        return invoices;
    }

    public void setInvoices(List<Invoice> cities) {
        this.invoices = cities;
    }

    public void add(Invoice invoice) {
        this.invoices.add(invoice);
    }
}