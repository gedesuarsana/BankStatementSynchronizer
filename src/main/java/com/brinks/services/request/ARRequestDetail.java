package com.brinks.services.request;

import java.math.BigDecimal;

public class ARRequestDetail {


    private String invoice_no;
    private BigDecimal amt;
    private String trx_status;

    public String getInvoice_no() {
        return invoice_no;
    }

    public void setInvoice_no(String invoice_no) {
        this.invoice_no = invoice_no;
    }

    public BigDecimal getAmt() {
        return amt;
    }

    public void setAmt(BigDecimal amt) {
        this.amt = amt;
    }

    public String getTrx_status() {
        return trx_status;
    }

    public void setTrx_status(String trx_status) {
        this.trx_status = trx_status;
    }
}


