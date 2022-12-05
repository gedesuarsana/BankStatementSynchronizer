package com.brinks;

import com.brinks.models.InvoiceStatus;
import com.brinks.repository.InvoiceStatusRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BankStatementSynchronizerApplication implements CommandLineRunner {

    @Value("${name}")
    private String name;

    @Autowired
    InvoiceStatusRepository invoiceStatusRepository;

    Logger logger = LoggerFactory.getLogger(BankStatementSynchronizerApplication.class);

    public static void main(String args[]) {
        SpringApplication.run(BankStatementSynchronizerApplication.class, args);
    }

    public void run(String... args) throws Exception {

        logger.info("Program Starting!!!");

        InvoiceStatus invoiceStatus = new InvoiceStatus();
        invoiceStatus.setInvoice("invoice1");
        invoiceStatus.setStatus("pending!");

        invoiceStatusRepository.save(invoiceStatus);

    }
}
