package com.brinks.services.request;


import java.util.List;

public class ARRequest {


    private String trx_date;
    private String trx_bank_code;
    private String trx_no_ref;
    private List<ARRequestDetail> trx_details;

    public String getTrx_date() {
        return trx_date;
    }

    public void setTrx_date(String trx_date) {
        this.trx_date = trx_date;
    }

    public String getTrx_bank_code() {
        return trx_bank_code;
    }

    public void setTrx_bank_code(String trx_bank_code) {
        this.trx_bank_code = trx_bank_code;
    }

    public String getTrx_no_ref() {
        return trx_no_ref;
    }

    public void setTrx_no_ref(String trx_no_ref) {
        this.trx_no_ref = trx_no_ref;
    }

    public List<ARRequestDetail> getTrx_details() {
        return trx_details;
    }

    public void setTrx_details(List<ARRequestDetail> trx_details) {
        this.trx_details = trx_details;
    }
}


