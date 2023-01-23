package com.brinks.models;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;

@Entity
@Table(name="bank_statement")
public class BankStatement {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)

    private BigInteger id;

    private String bank;

    private BigInteger transaction_file_id;

    private String account_number;

    private String transaction_type;

    private BigDecimal amount;

    private String statement;

    private String processed_status;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public void setTransaction_file_id(BigInteger transaction_file_id) {
        this.transaction_file_id = transaction_file_id;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getAccount_number() {
        return account_number;
    }

    public void setAccount_number(String account_number) {
        this.account_number = account_number;
    }

    public String getTransaction_type() {
        return transaction_type;
    }

    public void setTransaction_type(String transaction_type) {
        this.transaction_type = transaction_type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public String getProcessed_status() {
        return processed_status;
    }

    public void setProcessed_status(String processed_status) {
        this.processed_status = processed_status;
    }


}
