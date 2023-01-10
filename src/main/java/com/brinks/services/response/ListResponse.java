package com.brinks.services.response;

import java.util.ArrayList;

public class ListResponse {

    private ArrayList<Batch> batch;
    private String responseCode;

    public ArrayList<Batch> getBatch() {
        return batch;
    }

    public void setBatch(ArrayList<Batch> batch) {
        this.batch = batch;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMsg() {
        return responseMsg;
    }

    public void setResponseMsg(String responseMsg) {
        this.responseMsg = responseMsg;
    }

    private String responseMsg;
}


class Batch{
    private String batchnum;
    private String batchdate;
    private String batchdesc;
    private long amt;
    private String type;
    private String status;
    private String invtype;
    private ArrayList<Invoice> invoice;

    public String getBatchnum() {
        return batchnum;
    }

    public void setBatchnum(String batchnum) {
        this.batchnum = batchnum;
    }

    public String getBatchdate() {
        return batchdate;
    }

    public void setBatchdate(String batchdate) {
        this.batchdate = batchdate;
    }

    public String getBatchdesc() {
        return batchdesc;
    }

    public void setBatchdesc(String batchdesc) {
        this.batchdesc = batchdesc;
    }

    public long getAmt() {
        return amt;
    }

    public void setAmt(long amt) {
        this.amt = amt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getInvtype() {
        return invtype;
    }

    public void setInvtype(String invtype) {
        this.invtype = invtype;
    }

    public ArrayList<Invoice> getInvoice() {
        return invoice;
    }

    public void setInvoice(ArrayList<Invoice> invoice) {
        this.invoice = invoice;
    }
}

class Invoice{
    private String invnum;
    private String custnum;
    private String desc;
    private String date;
    private String curr;
    private int amt;
    private int amtnotax;

    public String getInvnum() {
        return invnum;
    }

    public void setInvnum(String invnum) {
        this.invnum = invnum;
    }

    public String getCustnum() {
        return custnum;
    }

    public void setCustnum(String custnum) {
        this.custnum = custnum;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCurr() {
        return curr;
    }

    public void setCurr(String curr) {
        this.curr = curr;
    }

    public int getAmt() {
        return amt;
    }

    public void setAmt(int amt) {
        this.amt = amt;
    }

    public int getAmtnotax() {
        return amtnotax;
    }

    public void setAmtnotax(int amtnotax) {
        this.amtnotax = amtnotax;
    }
}