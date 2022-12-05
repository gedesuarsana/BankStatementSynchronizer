package com.brinks;

import com.brinks.models.InvoiceStatus;
import com.brinks.repository.InvoiceStatusRepository;
import com.brinks.services.FTPService;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;


@SpringBootApplication
public class BankStatementSynchronizerApplication implements CommandLineRunner {

    @Value("${name}")
    private String name;

    @Autowired
    FTPService ftpService;

    @Autowired
    InvoiceStatusRepository invoiceStatusRepository;

    Logger logger = LoggerFactory.getLogger(BankStatementSynchronizerApplication.class);

    public static void main(String args[]) {
        SpringApplication.run(BankStatementSynchronizerApplication.class, args);
    }

    public void run(String... args) throws Exception {

        logger.info("Program Starting!!!");

        FTPClient ftpClient = ftpService.loginFtp();

        ftpService.printTree("/",ftpClient);

    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .build();
    }
}
