package com.brinks.services.impl;

import com.brinks.services.BrinksAPIService;
import com.brinks.services.response.InvoiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service("BrinksAPIService")
public class BrinksAPIServiceImpl implements BrinksAPIService {

    @Autowired
    RestTemplate restTemplate;

    @Override
    public List<InvoiceResponse> getAllInvoice() {
        //todo ask to the user how to call this api

        return null;
    }
}
