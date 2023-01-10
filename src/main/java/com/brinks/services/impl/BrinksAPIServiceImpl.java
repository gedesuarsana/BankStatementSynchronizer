package com.brinks.services.impl;

import com.brinks.services.BrinksAPIService;
import com.brinks.services.request.AuthenticationRequest;
import com.brinks.services.request.InquiryRequest;
import com.brinks.services.request.ListRequest;
import com.brinks.services.response.AuthenticationResponse;
import com.brinks.services.response.InquiryResponse;
import com.brinks.services.response.ListResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service("BrinksAPIService")
public class BrinksAPIServiceImpl implements BrinksAPIService {

    @Autowired
    RestTemplate restTemplate;


    @Value("${api.authentication.url}")
    private String authenticationURL;

    @Value("${api.authentication.authorization-header}")
    private String authenticationAuthorizationHeader;

    @Value("${api.authentication.vendorid}")
    private String authenticationVendorId;

    @Value("${api.authentication.systemid}")
    private String authenticationSystemId;

    @Value("${api.authentication.entityid}")
    private String authenticationEntityId;

    @Value("${api.authentication.userid}")
    private String authenticationUserId;

    @Value("${api.inquiry.url}")
    private String inquiryURL;

    @Value("${api.list.url}")
    private String listURL;




    public AuthenticationResponse authenticate(AuthenticationRequest request){

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", authenticationAuthorizationHeader);
        HttpEntity<AuthenticationRequest> entity = new HttpEntity<AuthenticationRequest>(request, headers);

        ResponseEntity<AuthenticationResponse> responseEntity = restTemplate.exchange(authenticationURL, HttpMethod.POST, entity, AuthenticationResponse.class);

        return responseEntity.getBody();
    }


    public InquiryResponse inquiry(String token, InquiryRequest request){

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Token", token);
        HttpEntity<InquiryRequest> entity = new HttpEntity<InquiryRequest>(request, headers);

        ResponseEntity<InquiryResponse> responseEntity = restTemplate.exchange(inquiryURL, HttpMethod.POST, entity, InquiryResponse.class);

        return responseEntity.getBody();

    }

    public ListResponse list(String token, ListRequest request){

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Token", token);
        HttpEntity<ListRequest> entity = new HttpEntity<ListRequest>(request, headers);

        ResponseEntity<ListResponse> responseEntity = restTemplate.exchange(listURL, HttpMethod.POST, entity, ListResponse.class);

        return responseEntity.getBody();
    }






}
