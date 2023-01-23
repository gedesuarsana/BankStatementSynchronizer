package com.brinks.services.request;

import java.util.ArrayList;

public class ARRequest {


    public String systemid;
    public String entityid;
    public String userid;
    public String batchdesc;
    public String batchdate;
    public ArrayList<Receipt> receipt;

    public String getSystemid() {
        return systemid;
    }

    public void setSystemid(String systemid) {
        this.systemid = systemid;
    }

    public String getEntityid() {
        return entityid;
    }

    public void setEntityid(String entityid) {
        this.entityid = entityid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getBatchdesc() {
        return batchdesc;
    }

    public void setBatchdesc(String batchdesc) {
        this.batchdesc = batchdesc;
    }

    public String getBatchdate() {
        return batchdate;
    }

    public void setBatchdate(String batchdate) {
        this.batchdate = batchdate;
    }

    public ArrayList<Receipt> getReceipt() {
        return receipt;
    }

    public void setReceipt(ArrayList<Receipt> receipt) {
        this.receipt = receipt;
    }
}

class Detail {
    public String invnum;
    public int amt;
    public int discamt;
    public String desc;
    public String ref;

    public String getInvnum() {
        return invnum;
    }

    public void setInvnum(String invnum) {
        this.invnum = invnum;
    }

    public int getAmt() {
        return amt;
    }

    public void setAmt(int amt) {
        this.amt = amt;
    }

    public int getDiscamt() {
        return discamt;
    }

    public void setDiscamt(int discamt) {
        this.discamt = discamt;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }
}

class Receipt {
    public String banknum;
    public String bankcurr;
    public String docdate;
    public String desc;
    public String custnum;
    public String remitnum;
    public String paycode;
    public String paytype;
    public String checknum;
    public String ref;
    public String curr;
    public int amt;
    public String applyto;
    public ArrayList<Detail> detail;

    public String getBanknum() {
        return banknum;
    }

    public void setBanknum(String banknum) {
        this.banknum = banknum;
    }

    public String getBankcurr() {
        return bankcurr;
    }

    public void setBankcurr(String bankcurr) {
        this.bankcurr = bankcurr;
    }

    public String getDocdate() {
        return docdate;
    }

    public void setDocdate(String docdate) {
        this.docdate = docdate;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCustnum() {
        return custnum;
    }

    public void setCustnum(String custnum) {
        this.custnum = custnum;
    }

    public String getRemitnum() {
        return remitnum;
    }

    public void setRemitnum(String remitnum) {
        this.remitnum = remitnum;
    }

    public String getPaycode() {
        return paycode;
    }

    public void setPaycode(String paycode) {
        this.paycode = paycode;
    }

    public String getPaytype() {
        return paytype;
    }

    public void setPaytype(String paytype) {
        this.paytype = paytype;
    }

    public String getChecknum() {
        return checknum;
    }

    public void setChecknum(String checknum) {
        this.checknum = checknum;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
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

    public String getApplyto() {
        return applyto;
    }

    public void setApplyto(String applyto) {
        this.applyto = applyto;
    }

    public ArrayList<Detail> getDetail() {
        return detail;
    }

    public void setDetail(ArrayList<Detail> detail) {
        this.detail = detail;
    }
}


