package com.brinks.services.response;

import java.math.BigDecimal;

public class InquiryResponse {
    private String response_code;
    private String response_message;
    private Detail body;

    public String getResponse_code() {
        return response_code;
    }

    public void setResponse_code(String response_code) {
        this.response_code = response_code;
    }

    public String getResponse_message() {
        return response_message;
    }

    public void setResponse_message(String response_message) {
        this.response_message = response_message;
    }

    public Detail getBody() {
        return body;
    }

    public void setBody(Detail body) {
        this.body = body;
    }
}




