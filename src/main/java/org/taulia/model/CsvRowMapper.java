package org.taulia.model;

import org.springframework.stereotype.Component;

@Component
public class CsvRowMapper {

    public static Invoice map(final String[] row) {
        Invoice invoice = new Invoice();
        invoice.setBuyer(row[0]);
        invoice.setImageName(row[1]);
        invoice.setDueDate(row[3]);
        invoice.setNumber(row[4]);
        invoice.setAmount(row[5]);
        invoice.setCurrency(row[6]);
        invoice.setStatus(row[7]);
        invoice.setSupplier(row[8]);
        return invoice;
    }
}
