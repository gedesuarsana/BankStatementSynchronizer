package com.brinks.services.impl;

import com.brinks.services.BrinksAPIService;
import com.brinks.services.request.ARRequest;
import com.brinks.services.request.AuthenticationRequest;
import com.brinks.services.request.InquiryRequest;
import com.brinks.services.request.ListRequest;
import com.brinks.services.response.ARResponse;
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


    RestTemplate restTemplate = new RestTemplate();


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

    @Value("${api.ar.url}")
    private String arURL;




    public AuthenticationResponse authenticate(AuthenticationRequest request){


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", authenticationAuthorizationHeader);
        HttpEntity<AuthenticationRequest> entity = new HttpEntity<AuthenticationRequest>(request, headers);

        ResponseEntity<AuthenticationResponse> responseEntity = restTemplate.exchange(authenticationURL, HttpMethod.POST, entity, AuthenticationResponse.class);

        return responseEntity.getBody();
    }

//    public AuthenticationResponse authenticate(){
//        AuthenticationRequest request = new AuthenticationRequest();
//        request.setEntityid(authenticationEntityId);
//        request.setSystemid(authenticationSystemId);
//        request.setVendorid(authenticationVendorId);
//        request.setUserid(authenticationUserId);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.set("Authorization", authenticationAuthorizationHeader);
//        HttpEntity<AuthenticationRequest> entity = new HttpEntity<AuthenticationRequest>(request, headers);
//
//        ResponseEntity<AuthenticationResponse> responseEntity = restTemplate.exchange(authenticationURL, HttpMethod.POST, entity, AuthenticationResponse.class);
//
//        return responseEntity.getBody();
//    }


    public AuthenticationResponse authenticate(){
        AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        authenticationResponse.setResponseCode("0");
        authenticationResponse.setResponseMsg("SUCCESS");
        authenticationResponse.setToken("Token12345");
        return authenticationResponse;
    }


//    public InquiryResponse inquiry(String token, InquiryRequest request){
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.set("Token", token);
//        HttpEntity<InquiryRequest> entity = new HttpEntity<InquiryRequest>(request, headers);
//
//        ResponseEntity<InquiryResponse> responseEntity = restTemplate.exchange(inquiryURL, HttpMethod.POST, entity, InquiryResponse.class);
//
//        return responseEntity.getBody();
//
//    }


    public InquiryResponse inquiry(String token, InquiryRequest request){

        InquiryResponse inquiryResponse = new InquiryResponse();
        inquiryResponse.setAmt("123121.12");
        inquiryResponse.setResponseCode("0");
        inquiryResponse.setResponseMsg("SUCCESS");


        return inquiryResponse;

    }

    public ListResponse list(String token, ListRequest request){

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Token", token);
        HttpEntity<ListRequest> entity = new HttpEntity<ListRequest>(request, headers);

        ResponseEntity<ListResponse> responseEntity = restTemplate.exchange(listURL, HttpMethod.POST, entity, ListResponse.class);

        return responseEntity.getBody();
    }


//    public ARResponse ar(String token, ARRequest request){
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.set("Token", token);
//        HttpEntity<ARRequest> entity = new HttpEntity<ARRequest>(request, headers);
//
//        ResponseEntity<ARResponse> responseEntity = restTemplate.exchange(arURL, HttpMethod.POST, entity, ARResponse.class);
//
//        return responseEntity.getBody();
//
//    }



    public ARResponse ar(String token, ARRequest request){
      ARResponse arResponse = new ARResponse();
      arResponse.setResponseCode("0");
      arResponse.setResponseMsg("SUCCESS");
        return arResponse;
    }


}
