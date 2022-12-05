package com.brinks.models;

import javax.persistence.*;

@Entity
@Table(name="invoice_status_tr")
public class InvoiceStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String invoice;

    private String status;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getInvoice() {
        return invoice;
    }

    public void setInvoice(String invoice) {
        this.invoice = invoice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
