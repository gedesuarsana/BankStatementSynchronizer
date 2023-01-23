package com.brinks.services;

import com.brinks.models.BankStatement;

import java.util.List;

public interface InvoiceService {

    public void extratInvoiceFromStatement(BankStatement bankStatement);
    public void processInvoice();
}
