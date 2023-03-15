package com.brinks.services.impl;

import com.brinks.services.BrinksAPIService;
import com.brinks.services.request.ARRequest;
import com.brinks.services.request.InquiryRequest;
import com.brinks.services.response.ARResponse;
import com.brinks.services.response.AuthenticationResponse;
import com.brinks.services.response.InquiryResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Collections;

@Service("BrinksAPIService")
public class BrinksAPIServiceImpl implements BrinksAPIService {


    RestTemplate restTemplate = new RestTemplate();


    @Value("${api.authentication.url}")
    private String authenticationURL;

    @Value("${api.authentication.client-id}")
    private String authenticationClientId;

    @Value("${api.authentication.client-secret}")
    private String authenticationClientSecret;

    @Value("${api.inquiry.url}")
    private String inquiryURL;

    @Value("${api.ar.url}")
    private String arURL;






    public AuthenticationResponse authenticate(){


        String authorization= "Basic "+Base64.getEncoder().encodeToString((authenticationClientId+":"+authenticationClientSecret).getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Authorization",authorization);

        MultiValueMap<String, String> bodyPair = new LinkedMultiValueMap();
        bodyPair.add("grant_type", "client_credentials");


        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<MultiValueMap<String, String>>(bodyPair, headers);

        ResponseEntity<AuthenticationResponse> responseEntity = restTemplate.exchange(authenticationURL, HttpMethod.POST, entity, AuthenticationResponse.class);

        return responseEntity.getBody();
    }




    public InquiryResponse inquiry(String token, InquiryRequest request){

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization","Bearer "+token);
        HttpEntity<InquiryRequest> entity = new HttpEntity<InquiryRequest>(request, headers);

        ResponseEntity<InquiryResponse> responseEntity = restTemplate.exchange(inquiryURL, HttpMethod.POST, entity, InquiryResponse.class);

        return responseEntity.getBody();

    }





    public ARResponse ar(String token, ARRequest request){

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization","Bearer "+token);
        HttpEntity<ARRequest> entity = new HttpEntity<ARRequest>(request, headers);

        ResponseEntity<ARResponse> responseEntity = restTemplate.exchange(arURL, HttpMethod.POST, entity, ARResponse.class);

        return responseEntity.getBody();

    }




}
