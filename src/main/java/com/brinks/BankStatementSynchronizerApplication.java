package com.brinks;

import com.brinks.models.InvoiceStatus;
import com.brinks.repository.InvoiceStatusRepository;
import com.brinks.services.FTPService;
import com.brinks.utils.CommonUtils;
import com.prowidesoftware.swift.model.field.Field61;
import com.prowidesoftware.swift.model.mt.mt9xx.MT940;
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

import java.util.Arrays;
import java.util.List;


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

        byte[] fileContent= ftpService.downloadFile("/RPT0167031757170902085684.txt",ftpClient);

        String content = new String(fileContent);

        List<String> item = CommonUtils.splitSwiftMessage(content);


        System.out.println("ukuran:"+item.size());
        for(int x=0;x<item.size();x++){
            System.out.println("No:"+(x+1));
            System.out.println(item.get(x));
            System.out.println("**********");
        }

        System.out.println(content);

        logger.info("Program End!!!");

    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .build();
    }
}
