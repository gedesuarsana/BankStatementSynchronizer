package com.brinks.services;

import com.prowidesoftware.swift.model.field.Field61;
import com.prowidesoftware.swift.model.field.Field86;

import java.math.BigInteger;

public interface BankStatementService {

    public void processStatement(String bank, String accountNumber, BigInteger transactionFile,Field61 field61, Field86 field86);
}
