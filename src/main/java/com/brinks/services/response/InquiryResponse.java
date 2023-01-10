package com.brinks.services.response;

import java.util.ArrayList;

public class InquiryResponse {
    private String docnum;
    private String custnum;
    private String invdesc;
    private String docdate;
    private String duedate;
    private String termcode;
    private String curr;
    private String amt;
    private String ordernum;
    private String custpo;

    public String getDocnum() {
        return docnum;
    }

    public void setDocnum(String docnum) {
        this.docnum = docnum;
    }

    public String getCustnum() {
        return custnum;
    }

    public void setCustnum(String custnum) {
        this.custnum = custnum;
    }

    public String getInvdesc() {
        return invdesc;
    }

    public void setInvdesc(String invdesc) {
        this.invdesc = invdesc;
    }

    public String getDocdate() {
        return docdate;
    }

    public void setDocdate(String docdate) {
        this.docdate = docdate;
    }

    public String getDuedate() {
        return duedate;
    }

    public void setDuedate(String duedate) {
        this.duedate = duedate;
    }

    public String getTermcode() {
        return termcode;
    }

    public void setTermcode(String termcode) {
        this.termcode = termcode;
    }

    public String getCurr() {
        return curr;
    }

    public void setCurr(String curr) {
        this.curr = curr;
    }

    public String getAmt() {
        return amt;
    }

    public void setAmt(String amt) {
        this.amt = amt;
    }

    public String getOrdernum() {
        return ordernum;
    }

    public void setOrdernum(String ordernum) {
        this.ordernum = ordernum;
    }

    public String getCustpo() {
        return custpo;
    }

    public void setCustpo(String custpo) {
        this.custpo = custpo;
    }

    public String getDoctype() {
        return doctype;
    }

    public void setDoctype(String doctype) {
        this.doctype = doctype;
    }

    public String getDocstatus() {
        return docstatus;
    }

    public void setDocstatus(String docstatus) {
        this.docstatus = docstatus;
    }

    public ArrayList<Optfld> getOptfld() {
        return optfld;
    }

    public void setOptfld(ArrayList<Optfld> optfld) {
        this.optfld = optfld;
    }

    public ArrayList<Detail> getDetail() {
        return detail;
    }

    public void setDetail(ArrayList<Detail> detail) {
        this.detail = detail;
    }

    public ArrayList<Receipt> getReceipt() {
        return receipt;
    }

    public void setReceipt(ArrayList<Receipt> receipt) {
        this.receipt = receipt;
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

    private String doctype;
    private String docstatus;
    private ArrayList<Optfld> optfld;
    private ArrayList<Detail> detail;
    private ArrayList<Receipt> receipt;
    private String responseCode;
    private String responseMsg;
}

class Detail{
    private String itemno;
    private String itemdesc;
    private String curr;
    private String amt;
    private String tax;
    private String acctrev;
    private String acctinv;
    private ArrayList<Optfld> optfld;

    public String getItemno() {
        return itemno;
    }

    public void setItemno(String itemno) {
        this.itemno = itemno;
    }

    public String getItemdesc() {
        return itemdesc;
    }

    public void setItemdesc(String itemdesc) {
        this.itemdesc = itemdesc;
    }

    public String getCurr() {
        return curr;
    }

    public void setCurr(String curr) {
        this.curr = curr;
    }

    public String getAmt() {
        return amt;
    }

    public void setAmt(String amt) {
        this.amt = amt;
    }

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

    public String getAcctrev() {
        return acctrev;
    }

    public void setAcctrev(String acctrev) {
        this.acctrev = acctrev;
    }

    public String getAcctinv() {
        return acctinv;
    }

    public void setAcctinv(String acctinv) {
        this.acctinv = acctinv;
    }

    public ArrayList<Optfld> getOptfld() {
        return optfld;
    }

    public void setOptfld(ArrayList<Optfld> optfld) {
        this.optfld = optfld;
    }
}

class Optfld {
    private String fld;
    private String val;

    public String getFld() {
        return fld;
    }

    public void setFld(String fld) {
        this.fld = fld;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }
}

class Receipt{
    private String bankname;
    private String remitno;
    private String dateremit;
    private String remitref;
    private String paycode;
    private String currrec;
    private String amtrec;
    private String curralloc;
    private String amtalloc;
    private String rectatus;

    public String getBankname() {
        return bankname;
    }

    public void setBankname(String bankname) {
        this.bankname = bankname;
    }

    public String getRemitno() {
        return remitno;
    }

    public void setRemitno(String remitno) {
        this.remitno = remitno;
    }

    public String getDateremit() {
        return dateremit;
    }

    public void setDateremit(String dateremit) {
        this.dateremit = dateremit;
    }

    public String getRemitref() {
        return remitref;
    }

    public void setRemitref(String remitref) {
        this.remitref = remitref;
    }

    public String getPaycode() {
        return paycode;
    }

    public void setPaycode(String paycode) {
        this.paycode = paycode;
    }

    public String getCurrrec() {
        return currrec;
    }

    public void setCurrrec(String currrec) {
        this.currrec = currrec;
    }

    public String getAmtrec() {
        return amtrec;
    }

    public void setAmtrec(String amtrec) {
        this.amtrec = amtrec;
    }

    public String getCurralloc() {
        return curralloc;
    }

    public void setCurralloc(String curralloc) {
        this.curralloc = curralloc;
    }

    public String getAmtalloc() {
        return amtalloc;
    }

    public void setAmtalloc(String amtalloc) {
        this.amtalloc = amtalloc;
    }

    public String getRectatus() {
        return rectatus;
    }

    public void setRectatus(String rectatus) {
        this.rectatus = rectatus;
    }
}


