package com.brinks;

import com.brinks.models.InvoiceStatus;
import com.brinks.repository.InvoiceStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Repository;

@SpringBootApplication
public class BankStatementSynchronizerApplication implements CommandLineRunner {

    @Value("${name}")
    private String name;

    @Autowired
    InvoiceStatusRepository invoiceStatusRepository;

    public static void main(String args[]) {
        SpringApplication.run(BankStatementSynchronizerApplication.class, args);
    }

    public void run(String... args) throws Exception {
        System.out.println("test-->"+name);

        InvoiceStatus invoiceStatus = new InvoiceStatus();
        invoiceStatus.setInvoice("invoice1");
        invoiceStatus.setStatus("pending!");

        invoiceStatusRepository.save(invoiceStatus);

    }
}
