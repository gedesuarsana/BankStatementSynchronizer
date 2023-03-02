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

    @Value("${statement.regex-extend}")
    String regexExtend;

    @Value("${statement.regex-repetition}")
    String regexRepetition;


    @Autowired
    InvoiceStatusRepository invoiceStatusRepository;


    @Autowired
    BankStatementRepository bankStatementRepository;


    @Autowired
    BrinksAPIService brinksAPIService;


    Logger logger = LoggerFactory.getLogger(InvoiceServiceImpl.class);


    @Override
    public void extratInvoiceFromStatement(BankStatement bankStatement) {

        String afterRemoveEnter = bankStatement.getStatement().replaceAll("\\r\\n"," ");
        String afterRemoveWhiteSpaceAndO = removeWhiteCharInInvoiceAndReplaceO(afterRemoveEnter);
        String afterrepe = uniformFormatRegexRepetition(afterRemoveWhiteSpaceAndO);
        String afterextend = uniformFormatRegexExtend(afterrepe);



        boolean found = false;
        int index=1;
        for (String regex : regexList) {
            logger.info("regex:" + regex);
            Pattern pattern = Pattern.compile(regex);

            Matcher matcher = pattern.matcher(afterextend);

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
                    invoiceStatus.setInvoice_name(reformatInvoiceName(invoice));
                    invoiceStatus.setIndex_in_statement(index);
                    invoiceStatusRepository.save(invoiceStatus);
                    logger.info("save invoice:"+invoiceStatus.getInvoice_name());
                    index++;
                }
            }

        }


        //process for common pattern

        Pattern commonPattern = Pattern.compile(regexCommon);

        Matcher commonMatcher = commonPattern.matcher(afterextend);

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


//    public Map<String, BigDecimal> getAccumulatedInvoice() {
//        Map<String, BigDecimal> result = new HashMap<String, BigDecimal>();
//
//        List<InvoiceStatus> invoiceStatusList = invoiceStatusRepository.findByStatus("INCOMPLETE");
//
//        for (InvoiceStatus item : invoiceStatusList) {
//            BankStatement bankStatement = bankStatementRepository.findById(item.getBank_statement_id()).get();
//
//
//            if (result.get(item.getInvoice_name()) == null) {
//                result.put(item.getInvoice_name(), bankStatement.getAmount());
//            } else {
//                BigDecimal amount = result.get(item.getInvoice_name());
//                BigDecimal newAmount = amount.add(bankStatement.getAmount());
//                result.put(item.getInvoice_name(), newAmount);
//            }
//        }
//        return result;
//    }


    public void processInvoice() {

        //Map<String, BigDecimal> invoices = getAccumulatedInvoice();

        List<InvoiceStatus> invoiceStatusList = invoiceStatusRepository.findByStatusOrderByIndexInStatementDesc("INCOMPLETE");

        //call the authentication Brinks API

        AuthenticationResponse authenticationResponse = brinksAPIService.authenticate();

        if (!Objects.isNull(authenticationResponse.getToken())) {

            for (InvoiceStatus invoiceStatus : invoiceStatusList) {

                // List<InvoiceStatus> invoiceStatusList = invoiceStatusRepository.findByInvoiceName(invoiceName);

                // finding the prevously invoice and sum
                List<InvoiceStatus> previousInvoiceList = invoiceStatusRepository.findByStatusAndInvoiceName("COMPLETED",invoiceStatus.getInvoice_name());

                BigDecimal totalPreviouslyAmount = new BigDecimal(0);
                for(InvoiceStatus item:previousInvoiceList){
                    BankStatement bankStatement = bankStatementRepository.findById(item.getBank_statement_id()).get();
                    totalPreviouslyAmount = totalPreviouslyAmount.add(bankStatement.getAmount());
                 }





                InquiryRequest inquiryRequest = new InquiryRequest();

                //todo where got the value for custnum
                inquiryRequest.setCustnum("?");

                inquiryRequest.setDocnum(invoiceStatus.getInvoice_name());

                InquiryResponse inquiryResponse = null;


                try {
                    inquiryResponse = brinksAPIService.inquiry(authenticationResponse.getToken(), inquiryRequest);

                    //save the response amount

                    invoiceStatus.setInquiry_amount(new BigDecimal(inquiryResponse.getAmt()));



                } catch (Exception e) {

                        invoiceStatus.setInquiry_status("ERROR");

                    e.printStackTrace();
                    logger.error("error:" + e.getMessage());
                }

                if (Objects.nonNull(inquiryResponse) && inquiryResponse.getResponseCode() == "0") {

                        invoiceStatus.setInquiry_status("COMPLETED");

                } else {

                        invoiceStatus.setInquiry_status("ERROR");

                }



                //process the amount

                BankStatement bankStatement = bankStatementRepository.findById(invoiceStatus.getBank_statement_id()).get();

                BigDecimal bankAmount = new BigDecimal(0);

                //many invoice in one line
                if(invoiceStatus.getIndex_in_statement()>1){
                 InvoiceStatus prevInvoiceStatus =    invoiceStatusRepository.findByBankStatementIdAndIndexInStatement(invoiceStatus.getBank_statement_id(),invoiceStatus.getIndex_in_statement()-1);

                    bankAmount = prevInvoiceStatus.getRemaining_amount().add(totalPreviouslyAmount);
                }else{
                    bankAmount = bankStatement.getAmount().add(totalPreviouslyAmount);
                }

                BigDecimal apiAmount = new BigDecimal(inquiryResponse.getAmt());
                BigDecimal apiAmountBeforeTax = apiAmount.divide(new BigDecimal(100).subtract(tax)).multiply(new BigDecimal(100));


                //set remaining amount
                invoiceStatus.setRemaining_amount(bankAmount.subtract(apiAmountBeforeTax));

                if (bankAmount.subtract(apiAmountBeforeTax).compareTo(new BigDecimal(0)) >= 0) {


                        invoiceStatus.setStatus("COMPLETED");

                    // call AR


                    ARResponse arResponse = null;
                    try {
                        ARRequest arRequest = new ARRequest();
                        //todo fullfill the parameter


                        arResponse = brinksAPIService.ar(authenticationResponse.getToken(), arRequest);

                    } catch (Exception e) {

                            invoiceStatus.setAr_status("ERROR");

                        e.printStackTrace();
                        logger.error("error:" + e.getMessage());
                    }

                    if (Objects.nonNull(arResponse) && arResponse.getResponseCode() == "0") {

                            invoiceStatus.setAr_status("COMPLETED");

                    }else{
                        logger.error("error: responseCode:"+arResponse);

                            invoiceStatus.setAr_status("ERROR");

                    }


                } else {

                        invoiceStatus.setStatus("PENDING");

                }


                // save all status
                invoiceStatusRepository.save(invoiceStatus);

            }
        }

    }

    private String reformatInvoiceName(String invoiceName) {
        String output;

        if (!invoiceName.contains("-")) {
            output= invoiceName.substring(0, 3) + "-" + invoiceName.substring(3);
        } else {
            output= invoiceName;
        }

        return  output.trim();
    }



    private String uniformFormatRegexExtend(String statement){

        logger.info("regex-extend:" + regexExtend);
        Pattern pattern = Pattern.compile(regexExtend);

        Matcher matcher = pattern.matcher(statement);

        boolean found = false;
        if (matcher.find()) {
            logger.info("I found the extend text " + matcher.group() + " starting at index " +
                    matcher.start() + " and ending at index " + matcher.end());
            found = true;


            //searching previous invoice pattern

            String prefixStatement = statement.substring(0,matcher.start());
            String suffixStatement = statement.substring(matcher.end(),statement.length());
            String token = matcher.group();


            logger.info("prev:"+prefixStatement);
            logger.info("suff:"+suffixStatement);

            int startPRV=0;
            int endPRV=0;

            for (String regex : regexList) {

                Pattern patternPRV = Pattern.compile(regex);

                Matcher matcherPRV = patternPRV.matcher("CF230200455_C01-000001");

                while (matcherPRV.find()) {
                    startPRV = matcherPRV.start();
                    endPRV = matcherPRV.end();
                }
            }

            logger.info("startPRV:"+startPRV);
            logger.info("endPRV:"+endPRV);

            String invoicePRV = statement.substring(startPRV,endPRV).trim();

            logger.info("prev-invoice:"+invoicePRV);


            // iterate invoice until end

            String invoicePRVNumberOnly = invoicePRV.replaceAll("[^0-9]", "");

            String until = invoicePRVNumberOnly.substring(0,invoicePRVNumberOnly.length()-(token.length()-1))+token.substring(1);

            int invoicePRVInt = Integer.parseInt(invoicePRVNumberOnly);
            int untilInt = Integer.parseInt(until);

            StringBuffer invoices = new StringBuffer();

            for(int x=(invoicePRVInt+1);x<=untilInt;x++){
                String invoice = ""+x;
                invoices.append(" "+invoicePRV.substring(0,3)+"-"+invoice.substring(invoice.length()-6,invoice.length())+" ");
            }

            String newStatement = prefixStatement+invoices+suffixStatement;

          return uniformFormatRegexExtend(newStatement);
        }else{
            logger.info("after regex-extend new statement:" + statement);
            return statement;
        }



    }



    private String uniformFormatRegexRepetition(String statement){

        logger.info("regex-repetition:" + regexRepetition);
        Pattern pattern = Pattern.compile(regexRepetition);

        Matcher matcher = pattern.matcher(statement);

        boolean found = false;
        if (matcher.find()) {
            logger.info("I found the repetition text " + matcher.group() + " starting at index " +
                    matcher.start() + " and ending at index " + matcher.end());
            found = true;


            //searching previous invoice pattern

            String prefixStatement = statement.substring(0,matcher.start());
            String suffixStatement = statement.substring(matcher.end(),statement.length());
            String token = matcher.group();


            logger.info("prev:"+prefixStatement);
            logger.info("suff:"+suffixStatement);

            int startPRV=0;
            int endPRV=0;

            for (String regex : regexList) {

                Pattern patternPRV = Pattern.compile(regex);

                Matcher matcherPRV = patternPRV.matcher(prefixStatement);

                while (matcherPRV.find()) {
                    startPRV = matcherPRV.start();
                    endPRV = matcherPRV.end();
                }
            }



            String invoicePRV = statement.substring(startPRV,endPRV).trim();

            logger.info("prev-invoice:"+invoicePRV);


            // iterate invoice until end

            String invoicePRVNumberOnly = invoicePRV.replaceAll("[^0-9]", "");

            String until = invoicePRVNumberOnly.substring(0,invoicePRVNumberOnly.length()-(token.length()-1))+token.substring(1);

            String newInvoice = " "+invoicePRV.substring(0,3)+"-"+until.substring(until.length()-6,until.length());

            String newStatement = prefixStatement+newInvoice+suffixStatement;

            return uniformFormatRegexRepetition(newStatement);
        }else{
            logger.info("after regex-repetition new statement:" + statement);
            return statement;
        }



    }



    private String removeWhiteCharInInvoiceAndReplaceO(String statement){
        Pattern pattern = Pattern.compile("[Cc][0-9oO]{2}");

        Matcher matcher = pattern.matcher(statement);


        while (matcher.find()) {

            int startChar = matcher.start();
            int invoiceLength = 9;

            for(int x=0;x<invoiceLength;x++) {
                char currentChar = statement.charAt(startChar + x);
                if (currentChar == ' ' || currentChar == '-') {
                    invoiceLength++;
                }
            }

            String prefixStatement = statement.substring(0,matcher.start());
            String invoice = statement.substring(matcher.start(),(matcher.start()+invoiceLength));
            invoice = invoice.replaceAll(" ","");
            invoice = invoice.replaceAll("[oO]","0");
            String suffixStatement = statement.substring((matcher.start()+invoiceLength),statement.length());
            statement = prefixStatement+invoice+suffixStatement;
        }
        return statement;
    }


    public static void main(String args[]){

       InvoiceServiceImpl x = new InvoiceServiceImpl();
       x.regexExtend ="_[0-9]{2}";
       x.regexRepetition=",[0-9]{2}";
       x.regexList = new ArrayList<>();
       x.regexList.add("[cC][0-9]{8}");
       x.regexList.add("[cC][0-9]{2}[-| ][0-9]{6}");



//        String afterrepe = x.uniformFormatRegexRepetition("CF220901571_C17-173332, C06-173269, C02- 173306_08");
//        String output = x.uniformFormatRegexExtend(afterrepe);


     //   String output = x.removeWhiteCharInInvoiceAndReplaceO("CF220901571_C17-173332, C06-173269, C02- 173306_08");



        int startPRV=0;
        int endPRV=0;

        for (String regex : x.regexList) {

            Pattern patternPRV = Pattern.compile(regex);

            Matcher matcherPRV = patternPRV.matcher("CF230200455_C01-000001");

            while (matcherPRV.find()) {
                startPRV = matcherPRV.start();
                endPRV = matcherPRV.end();
            }
        }

        String invoicePRV = "CF230200455_C01-000001".substring(startPRV,endPRV).trim();

       System.out.println("-->"+invoicePRV);




    }




//
//    public static void main(String args){
//
//        String messsage="BO TIKI JALUR NUGRAHA EKAKU BCA\\n" +
//                "C01-175549_50/SCBTJ-RP/OCT/22 IN99992211031655";
//    }


}
