package com.brinks;

import com.brinks.models.Transaction;
import com.brinks.repository.TransactionRepository;
import com.brinks.services.BankStatementService;
import com.brinks.services.FTPService;
import com.brinks.services.InvoiceService;
import com.brinks.utils.CommonUtils;
import com.prowidesoftware.swift.model.field.Field61;
import com.prowidesoftware.swift.model.field.Field86;
import com.prowidesoftware.swift.model.mt.mt9xx.MT940;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.Timestamp;
import java.util.List;


@SpringBootApplication
public class BankStatementSynchronizerApplication implements CommandLineRunner {

    @Value("${name}")
    private String name;

    @Autowired
    FTPService ftpService;

    @Autowired
    BankStatementService bankStatementService;

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    InvoiceService invoiceService;

    Logger logger = LoggerFactory.getLogger(BankStatementSynchronizerApplication.class);

    public static void main(String args[]) {
        SpringApplication.run(BankStatementSynchronizerApplication.class, args);
    }

    public void run(String... args) throws Exception {

        String bankCode = args[0];

        String pathFile = args[1];


        logger.info("Program Starting!!! with bank:" + bankCode + " filePath:" + pathFile);
        Transaction transaction = new Transaction();
        transaction.setFile_name(pathFile);
        transaction.setStatus("START");
        transaction.setStart_time(new Timestamp(System.currentTimeMillis()));
        Transaction transactionsaved = transactionRepository.save(transaction);

        FTPClient ftpClient = ftpService.loginFtp();

        byte[] fileContent = ftpService.downloadFile(pathFile, ftpClient);

        String content = new String(fileContent);

        List<String> swiftItem = CommonUtils.splitSwiftMessage(content);


        for (int x = 0; x < swiftItem.size(); x++) {

            MT940 mt940 = MT940.parse(swiftItem.get(x));
            String accountNumber = mt940.getField25().getAccount();

            logger.info("sessionNumber:" + mt940.getSessionNumber());

            List<Field61> field61List = mt940.getField61();
            List<Field86> field86List = mt940.getField86();

            for (int y = 0; y < field61List.size(); y++) {
                bankStatementService.processStatement(bankCode, accountNumber, transactionsaved.getId(), field61List.get(y), field86List.get(y));
            }
        }


        invoiceService.processInvoice();


        transactionsaved.setStatus("END");
        transactionsaved.setEnd_time(new Timestamp(System.currentTimeMillis()));
        transactionRepository.save(transactionsaved);
        logger.info("Program End!!!");

    }


}
