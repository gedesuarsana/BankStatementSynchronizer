package com.brinks.services;

import com.brinks.services.request.ARRequest;
import com.brinks.services.request.InquiryRequest;
import com.brinks.services.response.ARResponse;
import com.brinks.services.response.AuthenticationResponse;
import com.brinks.services.response.InquiryResponse;



public interface BrinksAPIService {
    public AuthenticationResponse authenticate();
    public InquiryResponse inquiry(String token, InquiryRequest request);
    public ARResponse ar(String token, ARRequest request);
}
