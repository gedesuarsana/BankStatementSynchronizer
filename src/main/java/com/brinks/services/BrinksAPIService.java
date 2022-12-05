package com.brinks.services;

import com.brinks.services.response.InvoiceResponse;

import java.util.List;

public interface BrinksAPIService {
    public List<InvoiceResponse> getAllInvoice();
}
