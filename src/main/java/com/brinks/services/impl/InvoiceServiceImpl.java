package com.brinks.services.impl;


import com.brinks.models.BankStatement;
import com.brinks.models.InvoiceStatus;
import com.brinks.repository.BankStatementRepository;
import com.brinks.repository.InvoiceStatusRepository;
import com.brinks.services.BrinksAPIService;
import com.brinks.services.InvoiceService;
import com.brinks.services.request.ARRequest;
import com.brinks.services.request.InquiryRequest;
import com.brinks.services.response.ARResponse;
import com.brinks.services.response.AuthenticationResponse;
import com.brinks.services.response.InquiryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service("InvoiceService")
public class InvoiceServiceImpl implements InvoiceService {


    @Value("#{'${statement.regex-list}'.split('>>>')}")
    List<String> regexList;

    @Value("${app.tax-percentage}")
    private BigDecimal tax;

    @Value("${statement.regex-common}")
    String regexCommon;


    @Autowired
    InvoiceStatusRepository invoiceStatusRepository;


    @Autowired
    BankStatementRepository bankStatementRepository;


    @Autowired
    BrinksAPIService brinksAPIService;


    Logger logger = LoggerFactory.getLogger(InvoiceServiceImpl.class);


    @Override
    public void extratInvoiceFromStatement(BankStatement bankStatement) {

        boolean found = false;
        for (String regex : regexList) {
            logger.info("regex:" + regex);
            Pattern pattern = Pattern.compile(regex);

            Matcher matcher = pattern.matcher(bankStatement.getStatement());

            Set<String> invoiceName = new HashSet<>();
            while (matcher.find()) {
                logger.info("I found the text " + matcher.group() + " starting at index " +
                        matcher.start() + " and ending at index " + matcher.end());
                found = true;
                invoiceName.add(matcher.group());
            }

            if (!found) {
                logger.info("No match found.");
            } else {
                for (String invoice : invoiceName) {
                    InvoiceStatus invoiceStatus = new InvoiceStatus();
                    invoiceStatus.setBank_statement_id(bankStatement.getId());
                    invoiceStatus.setStatus("INCOMPLETE");
                    invoiceStatus.setAr_status("INCOMPLETE");
                    invoiceStatus.setInquiry_status("INCOMPLETE");
                    invoiceStatus.setInvoice_name(invoice);
                    invoiceStatusRepository.save(invoiceStatus);
                }
            }

        }


        //process for common pattern

        Pattern commonPattern = Pattern.compile(regexCommon);

        Matcher commonMatcher = commonPattern.matcher(bankStatement.getStatement());

        boolean commonFound = false;
        while (commonMatcher.find()) {
            logger.info("I found the common text " + commonMatcher.group() + " starting at index " +
                    commonMatcher.start() + " and ending at index " + commonMatcher.end());
            commonFound = true;
        }

        if (!commonFound) {
            logger.info("No common match found.");
        } else {
            bankStatement.setProcessed_status("HOLD");
        }


        // update the statement status
        if (found && commonFound) {
            bankStatement.setProcessed_status("SUCCESS");
        } else if (!found && commonFound) {
            bankStatement.setProcessed_status("HOLD");
        } else {
            bankStatement.setProcessed_status("IGNORE");
        }

    }


    public Map<String, BigDecimal> getAccumulatedInvoice() {
        Map<String, BigDecimal> result = new HashMap<String, BigDecimal>();

        List<InvoiceStatus> invoiceStatusList = invoiceStatusRepository.findByStatus("INCOMPLETE");

        for (InvoiceStatus item : invoiceStatusList) {
            BankStatement bankStatement = bankStatementRepository.findById(item.getBank_statement_id()).get();


            if (result.get(item.getInvoice_name()) == null) {
                result.put(item.getInvoice_name(), bankStatement.getAmount());
            } else {
                BigDecimal amount = result.get(item.getInvoice_name());
                BigDecimal newAmount = amount.add(bankStatement.getAmount());
                result.put(item.getInvoice_name(), newAmount);
            }
        }
        return result;
    }


    public void processInvoice() {

        Map<String, BigDecimal> invoices = getAccumulatedInvoice();

        //call the authentication Brinks API

        AuthenticationResponse authenticationResponse = brinksAPIService.authenticate();

        if (!Objects.isNull(authenticationResponse.getToken())) {
            for (String invoiceName : invoices.keySet()) {
                List<InvoiceStatus> invoiceStatusList = invoiceStatusRepository.findByInvoiceName(invoiceName);


                InquiryRequest inquiryRequest = new InquiryRequest();

                //todo where got the value for custnum
                inquiryRequest.setCustnum("?");

                inquiryRequest.setDocnum(reformatInvoiceName(invoiceName));

                InquiryResponse inquiryResponse = null;


                try {
                    inquiryResponse = brinksAPIService.inquiry(authenticationResponse.getToken(), inquiryRequest);

                } catch (Exception e) {
                    for (InvoiceStatus item : invoiceStatusList) {
                        item.setInquiry_status("ERROR");
                    }
                    e.printStackTrace();
                    logger.error("error:" + e.getMessage());
                }

                if (Objects.nonNull(inquiryResponse) && inquiryResponse.getResponseCode() == "0") {
                    for (InvoiceStatus item : invoiceStatusList) {
                        item.setInquiry_status("COMPLETED");
                    }
                } else {
                    logger.error("error: responseCode:"+inquiryResponse);
                    for (InvoiceStatus item : invoiceStatusList) {
                        item.setInquiry_status("ERROR");
                    }
                }


                //process the amount

                BigDecimal bankAmount = invoices.get(invoiceName);

                BigDecimal apiAmount = new BigDecimal(inquiryResponse.getAmt());
                BigDecimal apiAmountBeforeTax = apiAmount.divide(new BigDecimal(100).subtract(tax)).multiply(new BigDecimal(100));

                if (apiAmountBeforeTax.subtract(bankAmount).compareTo(new BigDecimal(0)) <= 0) {

                    for (InvoiceStatus item : invoiceStatusList) {
                        item.setStatus("COMPLETED");
                    }
                    // call AR


                    ARResponse arResponse = null;
                    try {
                        ARRequest arRequest = new ARRequest();
                        //todo fullfill the parameter


                        arResponse = brinksAPIService.ar(authenticationResponse.getToken(), arRequest);

                    } catch (Exception e) {
                        for (InvoiceStatus item : invoiceStatusList) {
                            item.setAr_status("ERROR");
                        }
                        e.printStackTrace();
                        logger.error("error:" + e.getMessage());
                    }

                    if (Objects.nonNull(arResponse) && arResponse.getResponseCode() == "0") {
                        for (InvoiceStatus item : invoiceStatusList) {
                            item.setAr_status("COMPLETED");
                        }
                    }else{
                        logger.error("error: responseCode:"+arResponse);
                        for (InvoiceStatus item : invoiceStatusList) {
                            item.setAr_status("ERROR");
                        }
                    }


                } else {
                    for (InvoiceStatus item : invoiceStatusList) {
                        item.setStatus("PENDING");
                    }
                }


                // save all status
                invoiceStatusRepository.saveAll(invoiceStatusList);

            }
        }

    }

    private String reformatInvoiceName(String invoiceName) {
        if (!invoiceName.contains("-")) {
            return invoiceName.substring(0, 3) + "-" + invoiceName.substring(3);
        } else {
            return invoiceName;
        }
    }


}
