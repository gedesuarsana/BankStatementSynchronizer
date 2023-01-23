package com.brinks.services;

import com.brinks.services.request.ARRequest;
import com.brinks.services.request.AuthenticationRequest;
import com.brinks.services.request.InquiryRequest;
import com.brinks.services.request.ListRequest;
import com.brinks.services.response.ARResponse;
import com.brinks.services.response.AuthenticationResponse;
import com.brinks.services.response.InquiryResponse;
import com.brinks.services.response.ListResponse;

import java.util.List;

public interface BrinksAPIService {
    public AuthenticationResponse authenticate(AuthenticationRequest request);
    public AuthenticationResponse authenticate();
    public InquiryResponse inquiry(String token, InquiryRequest request);
    public ListResponse list(String token, ListRequest request);
    public ARResponse ar(String token, ARRequest request);
}
