package com.brinks.services.impl;

import com.brinks.models.BankStatement;
import com.brinks.repository.BankStatementRepository;
import com.brinks.repository.InvoiceStatusRepository;
import com.brinks.services.BankStatementService;
import com.brinks.services.InvoiceService;
import com.prowidesoftware.swift.model.field.Field61;
import com.prowidesoftware.swift.model.field.Field86;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;


@Service("BankStatementService")
public class BankStatementServiceImpl implements BankStatementService {

    @Autowired
    InvoiceService invoiceService;

    @Autowired
    BankStatementRepository bankStatementRepository;

    Logger logger = LoggerFactory.getLogger(BankStatementServiceImpl.class);


    @Override
    public void processStatement(String bank, String accountNumber, BigInteger transactionFile,Field61 field61, Field86 field86) {

        BankStatement bankStatement = new BankStatement();
        bankStatement.setAccount_number(accountNumber);
        bankStatement.setBank(bank);
        bankStatement.setAmount(field61.getAmountAsBigDecimal());
        bankStatement.setProcessed_status("INPROGRESS");
        bankStatement.setTransaction_file_id(transactionFile);
        bankStatement.setStatement(field86.getValue());
        bankStatement.setTransaction_type(field61.getDebitCreditMark());
        BankStatement bankStatementSaved =bankStatementRepository.save(bankStatement);

        logger.info("save statement:"+field86.getValue());


        invoiceService.extratInvoiceFromStatement(bankStatement);

        bankStatementRepository.save(bankStatementSaved);









    }







}
