package com.brinks;

import com.brinks.models.InvoiceStatus;
import com.brinks.repository.InvoiceStatusRepository;
import com.brinks.services.FTPService;
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


        MT940 mt940 = MT940.parse("{1:F01WPACAU2SAXXX0000000000}{2:I940WIBSXXXXN}{4:\n" +
                ":20:CSCT032000000007\n" +
                ":25:032000000007\n" +
                ":28C:00029/001\n" +
                ":60F:C190205AUD0,00\n" +
                ":61:1902080208D5000,00F803Payer Name This //0802198032003412\n" +
                "is the beneficiary descrip\n" +
                ":86:WITHDRAWAL    2003412\n" +
                "Payer Name This is the beneficiary descrip\n" +
                ":61:1902080208D8000,00F817Payee Name This //0802198171413245\n" +
                "is the beneficiary descrip\n" +
                ":86:WITHDRAWAL-OSKO PAYMENT 1413245\n" +
                "Payee Name This is the beneficiary descrip\n" +
                ":61:1902080208D780,00F8702413480 07 Feb 2//0802198701413245\n" +
                "019 MD06 Requested by paye\n" +
                ":86:WITHDRAWAL-PAYMENT RETURN\n" +
                "2413480 07 Feb 2019 MD06 Requested by paye\n" +
                ":61:1902080208D4000,00F8742413481 07 Feb 2//0802198741413245\n" +
                "019 MD06 Requested by paye\n" +
                ":86:WITHDRAWAL-OSKO PAYMENT RETURN\n" +
                "2413481 07 Feb 2019 MD06 Requested by paye\n" +
                ":61:1902080208C6000,00F886Payee Name This //0802198862056575\n" +
                "is the beneficiary descrip\n" +
                ":86:DEPOSIT 2056575\n" +
                "Payee Name This is the beneficiary descrip\n" +
                ":61:1902080208C5000,00F887Payer Name This //0802198872003412\n" +
                "is the beneficiary descrip\n" +
                ":86:DEPOSIT-OSKO PAYMENT    2003412\n" +
                "Payer Name This is the beneficiary descrip\n" +
                ":61:1902080208C5000,00F8911286995 05 Feb 2//0802198911413245\n" +
                "019 BE05 Payee is not fami\n" +
                ":86:DEPOSIT-PAYMENT    RETURN\n" +
                "1286995 05 Feb 2019 BE05 Payee is not fami\n" +
                ":61:1902080208C8000,00F8921286995 05 Feb 2//0802198921413245\n" +
                "019 BE05 Payee is not fami\n" +
                ":86:DEPOSIT-OSKO PAYMENT    RETURN\n" +
                "1286995 05 Feb 2019 BE05 Payee is not fami\n" +
                ":61:1902080208C5000,00F8952003412 07 Feb 2//0802198951413245\n" +
                "019 AC07 Account closed En\n" +
                ":86:DEPOSIT-PAYMENT    REVERSAL\n" +
                "2003412 07 Feb 2019 AC07 Account closed En\n" +
                ":61:1902080208C8000,00F8965642137 07 Feb 2//0802198961413245\n" +
                "019 AC07 Account closed En\n" +
                ":86:DEPOSIT-OSKO PAYMENT    REVERSAL\n" +
                "5642137 07 Feb 2019 AC07 Account closed En\n" +
                ":62F:C190205AUD12000,00\n" +
                ":64:C190205AUD12000,00\n" +
                "-}");

        List<Field61> fields = mt940.getField61();

        for(Field61 item : fields){
            System.out.println("-->"+item.getValue());
            System.out.println("");
        }


    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .build();
    }
}
