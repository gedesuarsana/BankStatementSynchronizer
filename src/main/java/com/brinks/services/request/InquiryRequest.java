package com.brinks.services.request;

public class InquiryRequest {
    private String systemid;
    private String userid;
    private String entityid;
    private String custnum;
    private String docnum;

    public String getSystemid() {
        return systemid;
    }

    public void setSystemid(String systemid) {
        this.systemid = systemid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getEntityid() {
        return entityid;
    }

    public void setEntityid(String entityid) {
        this.entityid = entityid;
    }

    public String getCustnum() {
        return custnum;
    }

    public void setCustnum(String custnum) {
        this.custnum = custnum;
    }

    public String getDocnum() {
        return docnum;
    }

    public void setDocnum(String docnum) {
        this.docnum = docnum;
    }
}
