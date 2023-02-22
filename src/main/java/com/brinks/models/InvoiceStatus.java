package com.brinks.models;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;

@Entity
@Table(name="invoice_status")
public class InvoiceStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private BigInteger id;

    private String invoice_name;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public BigInteger getBank_statement_id() {
        return bank_statement_id;
    }

    public void setBank_statement_id(BigInteger bank_statement_id) {
        this.bank_statement_id = bank_statement_id;
    }

    private BigInteger bank_statement_id;

    private String status;

    private String inquiry_status;

    private String ar_status;

    private Integer index_in_statement;

    public Integer getIndex_in_statement() {
        return index_in_statement;
    }

    public void setIndex_in_statement(Integer index_in_statement) {
        this.index_in_statement = index_in_statement;
    }

    private BigDecimal inquiry_amount;

    public BigDecimal getInquiry_amount() {
        return inquiry_amount;
    }

    public void setInquiry_amount(BigDecimal inquiry_amount) {
        this.inquiry_amount = inquiry_amount;
    }



    private BigDecimal remaining_amount;

    public BigDecimal getRemaining_amount() {
        return remaining_amount;
    }

    public void setRemaining_amount(BigDecimal remaining_amount) {
        this.remaining_amount = remaining_amount;
    }

    public String getInvoice_name() {
        return invoice_name;
    }

    public void setInvoice_name(String invoice_name) {
        this.invoice_name = invoice_name;
    }



    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getInquiry_status() {
        return inquiry_status;
    }

    public void setInquiry_status(String inquiry_status) {
        this.inquiry_status = inquiry_status;
    }

    public String getAr_status() {
        return ar_status;
    }

    public void setAr_status(String ar_status) {
        this.ar_status = ar_status;
    }
}
