package com.brinks.services.response;

import java.math.BigDecimal;

public class Detail {
    private String invoice_no;
    private BigDecimal total_payment;

    public String getInvoice_no() {
        return invoice_no;
    }

    public void setInvoice_no(String invoice_no) {
        this.invoice_no = invoice_no;
    }

    public BigDecimal getTotal_payment() {
        return total_payment;
    }

    public void setTotal_payment(BigDecimal total_payment) {
        this.total_payment = total_payment;
    }
}
