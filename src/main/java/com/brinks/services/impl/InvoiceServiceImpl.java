package com.brinks.services.impl;


import com.brinks.models.BankStatement;
import com.brinks.models.InvoiceStatus;
import com.brinks.repository.BankStatementRepository;
import com.brinks.repository.InvoiceStatusRepository;
import com.brinks.services.BrinksAPIService;
import com.brinks.services.InvoiceService;
import com.brinks.services.request.ARRequest;
import com.brinks.services.request.ARRequestDetail;
import com.brinks.services.request.InquiryRequest;
import com.brinks.services.response.ARResponse;
import com.brinks.services.response.AuthenticationResponse;
import com.brinks.services.response.InquiryResponse;
import com.brinks.utils.InvoiceComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.math.BigDecimal;
import java.math.BigInteger;
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

    @Value("${statement.regex-extend2}")
    String regexExtend2;


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
        String afterextend2 = uniformFormatRegexExtend2(afterextend);



        boolean found = false;
        int index=1;
        for (String regex : regexList) {
            logger.info("regex:" + regex);
            Pattern pattern = Pattern.compile(regex);

            Matcher matcher = pattern.matcher(afterextend2);

            Set<String> invoiceName = new TreeSet<String>();
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

        Matcher commonMatcher = commonPattern.matcher(afterextend2);

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





    public void processInvoice() {

        //Map<String, BigDecimal> invoices = getAccumulatedInvoice();

        List<InvoiceStatus> invoiceStatusList = invoiceStatusRepository.findByStatusOrderByIndexInStatementDesc("INCOMPLETE");

        //call the authentication Brinks API

        AuthenticationResponse authenticationResponse = brinksAPIService.authenticate();

        if (!Objects.isNull(authenticationResponse.getAccess_token())) {


            Map<BigInteger,List<InvoiceStatus>> statementInvoiceStatusMap = new HashMap<>();



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



                inquiryRequest.setInvoice_no(invoiceStatus.getInvoice_name());

                InquiryResponse inquiryResponse = null;


                try {
                    inquiryResponse = brinksAPIService.inquiry(authenticationResponse.getAccess_token(), inquiryRequest);

                    //save the response amount



                    invoiceStatus.setInquiry_amount( inquiryResponse.getBody().getTotal_payment());



                }catch (HttpClientErrorException hcee){

                   String message= hcee.getMessage();
                    hcee.printStackTrace();
                    logger.error("error:" + hcee.getMessage());
                }catch (Exception e) {

                    invoiceStatus.setInquiry_status("ERROR");
                    e.printStackTrace();
                    logger.error("error:" + e.getMessage());
                }

                if (Objects.nonNull(inquiryResponse) && "20001".equalsIgnoreCase(inquiryResponse.getResponse_code())) {

                        invoiceStatus.setInquiry_status("COMPLETED");

                } else {

                        invoiceStatus.setInquiry_status("NOT FOUND");
                        invoiceStatusRepository.save(invoiceStatus);
                        continue;
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

                BigDecimal apiAmount = inquiryResponse.getBody().getTotal_payment();
               // BigDecimal apiAmountAfterTax = apiAmount.divide(new BigDecimal(100)).multiply(new BigDecimal(100).add(tax));


                //set remaining amount
                invoiceStatus.setRemaining_amount(bankAmount.subtract(apiAmount));



                Boolean hasNextInvoice = invoiceStatusRepository.findByBankStatementIdAndIndexInStatement(invoiceStatus.getBank_statement_id(),invoiceStatus.getIndex_in_statement()+1)!=null;

                if(hasNextInvoice) {
                    List<InvoiceStatus> invoiceStatusListBasedOnStatement = statementInvoiceStatusMap.get(invoiceStatus.getBank_statement_id());

                    if (Objects.isNull(invoiceStatusListBasedOnStatement)) {
                        invoiceStatusListBasedOnStatement = new ArrayList<>();
                        invoiceStatusListBasedOnStatement.add(invoiceStatus);
                        statementInvoiceStatusMap.put(invoiceStatus.getBank_statement_id(),invoiceStatusListBasedOnStatement);
                    }else{
                        invoiceStatusListBasedOnStatement.add(invoiceStatus);
                    }
                    //skip send into the ar when it multi invoice in one statement
                    invoiceStatusRepository.save(invoiceStatus);
                continue;
                }else{
                    Boolean hasPrevInvoice = invoiceStatusRepository.findByBankStatementIdAndIndexInStatement(invoiceStatus.getBank_statement_id(),invoiceStatus.getIndex_in_statement()-1)!=null;
                    if(hasPrevInvoice){
                        // treat for multi invoice in one statement

                        List<InvoiceStatus> allMultiInvoice=   statementInvoiceStatusMap.get(invoiceStatus.getBank_statement_id());
                        allMultiInvoice.add(invoiceStatus);


                        // call AR multi

                        ARResponse arResponse = null;
                        try {
                            ARRequest arRequest = new ARRequest();

                            arRequest.setTrx_bank_code(bankStatement.getBank());

                            arRequest.setTrx_date(bankStatement.getTransaction_date());


                            arRequest.setTrx_no_ref(""+invoiceStatus.getId());



                            List<ARRequestDetail> arRequestDetails = new ArrayList<>();

                            BigDecimal prevAmount = new BigDecimal(0);
                             int index=0;
                            for(InvoiceStatus invoiceStatusItem:allMultiInvoice) {

                                boolean isLastInvoice = index == (allMultiInvoice.size() -1)?true:false;

                                ARRequestDetail item = new ARRequestDetail();

                               if( invoiceStatusItem.getRemaining_amount().compareTo(new BigDecimal(0))>=0 && !isLastInvoice){
                                   invoiceStatusItem.setStatus("COMPLETED");
                                   item.setAmt(invoiceStatusItem.getInquiry_amount());
                               } else{

                                   if( invoiceStatusItem.getRemaining_amount().compareTo(new BigDecimal(0))==0){
                                       invoiceStatusItem.setStatus("COMPLETED");
                                   }else{
                                       invoiceStatusItem.setStatus("PENDING");
                                   }
                                   item.setAmt(prevAmount);
                               }


                                item.setInvoice_no(invoiceStatusItem.getInvoice_name());
                                item.setTrx_status("COMPLETED".equalsIgnoreCase(invoiceStatusItem.getStatus()) ? "paid" : "unsettled");


                                arRequestDetails.add(item);
                                invoiceStatusRepository.save(invoiceStatusItem);
                                prevAmount =invoiceStatusItem.getRemaining_amount().compareTo(new BigDecimal(0))>=0?invoiceStatusItem.getRemaining_amount():new BigDecimal(0);
                              index++;
                            }

                            arRequest.setTrx_details(arRequestDetails);

                            arResponse = brinksAPIService.ar(authenticationResponse.getAccess_token(), arRequest);

                        } catch (Exception e) {

                            for(InvoiceStatus invoiceStatusItem:allMultiInvoice) {
                                invoiceStatusItem.setAr_status("ERROR");
                                invoiceStatusRepository.save(invoiceStatusItem);
                            }

                            e.printStackTrace();
                            logger.error("error:" + e.getMessage());
                        }

                        if (Objects.nonNull(arResponse) && "20001".equalsIgnoreCase(arResponse.getResponse_code())) {

                            for(InvoiceStatus invoiceStatusItem:allMultiInvoice) {
                                invoiceStatusItem.setAr_status("COMPLETED");
                                invoiceStatusRepository.save(invoiceStatusItem);
                            }

                        }else{
                            logger.error("error: responseCode:"+arResponse);

                            for(InvoiceStatus invoiceStatusItem:allMultiInvoice) {
                                invoiceStatusItem.setAr_status("ERROR");
                                invoiceStatusRepository.save(invoiceStatusItem);
                            }

                        }





                    }else{
                        // treat for single invoice in one statement;


                        if(bankAmount.subtract(apiAmount).compareTo(new BigDecimal(0)) == 0 ){
                            invoiceStatus.setStatus("COMPLETED");
                        }else{
                            invoiceStatus.setStatus("PENDING");
                        }

                        // call AR

                        ARResponse arResponse = null;
                        try {
                            ARRequest arRequest = new ARRequest();

                            arRequest.setTrx_bank_code(bankStatement.getBank());

                            arRequest.setTrx_date(bankStatement.getTransaction_date());


                            arRequest.setTrx_no_ref(""+invoiceStatus.getId());
                            List<ARRequestDetail> arRequestDetails = new ArrayList<>();
                            ARRequestDetail item = new ARRequestDetail();
                            item.setAmt(bankAmount);
                            item.setInvoice_no(invoiceStatus.getInvoice_name());
                            item.setTrx_status("COMPLETED".equalsIgnoreCase(invoiceStatus.getStatus())?"paid":"unsettled");
                            arRequestDetails.add(item);
                            arRequest.setTrx_details(arRequestDetails);

                            arResponse = brinksAPIService.ar(authenticationResponse.getAccess_token(), arRequest);

                        } catch (Exception e) {

                            invoiceStatus.setAr_status("ERROR");

                            e.printStackTrace();
                            logger.error("error:" + e.getMessage());
                        }

                        if (Objects.nonNull(arResponse) && "20001".equalsIgnoreCase(arResponse.getResponse_code())) {

                            invoiceStatus.setAr_status("COMPLETED");

                        }else{
                            logger.error("error: responseCode:"+arResponse);

                            invoiceStatus.setAr_status("ERROR");

                        }

                        invoiceStatusRepository.save(invoiceStatus);


                        //end call single item
                    }
                }


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

                Matcher matcherPRV = patternPRV.matcher(prefixStatement);

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


    private String uniformFormatRegexExtend2(String statement){

        logger.info("regex-extend2:" + regexExtend2);
        Pattern pattern = Pattern.compile(regexExtend2);

        Matcher matcher = pattern.matcher(statement);

        boolean found = false;
        if (matcher.find()) {
            logger.info("I found the extend2 text " + matcher.group() + " starting at index " +
                    matcher.start() + " and ending at index " + matcher.end());
            found = true;


            //searching previous invoice pattern

            String prefixStatement = statement.substring(0,matcher.start()+6);
            String suffixStatement = statement.substring(matcher.end(),statement.length());
            String token = matcher.group().substring(6);


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

            return uniformFormatRegexExtend2(newStatement);
        }else{
            logger.info("after regex-extend2 new statement:" + statement);
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


//    public static void main(String args[]){
//
//       InvoiceServiceImpl x = new InvoiceServiceImpl();
//       x.regexExtend ="_[0-9]{2}";
//       x.regexRepetition=",[0-9]{2}";
//       x.regexList = new ArrayList<>();
//       x.regexList.add("[cC][0-9]{8}");
//       x.regexList.add("[cC][0-9]{2}[-| ][0-9]{6}");
//
//
//
////        String afterrepe = x.uniformFormatRegexRepetition("CF220901571_C17-173332, C06-173269, C02- 173306_08");
////        String output = x.uniformFormatRegexExtend(afterrepe);
//
//
//     //   String output = x.removeWhiteCharInInvoiceAndReplaceO("CF220901571_C17-173332, C06-173269, C02- 173306_08");
//
//
//
//        int startPRV=0;
//        int endPRV=0;
//
//        for (String regex : x.regexList) {
//
//            Pattern patternPRV = Pattern.compile(regex);
//
//            Matcher matcherPRV = patternPRV.matcher("CF230200455_C01-000001");
//
//            while (matcherPRV.find()) {
//                startPRV = matcherPRV.start();
//                endPRV = matcherPRV.end();
//            }
//        }
//
//        String invoicePRV = "CF230200455_C01-000001".substring(startPRV,endPRV).trim();
//
//       System.out.println("-->"+invoicePRV);
//
//
//
//
//    }





    public static void main(String args[]){

       // Set<String> invoiceName = new TreeSet<>(new InvoiceComparator());

        Set<String> invoiceName = new TreeSet<>();


        invoiceName.add("c01-000001");
        invoiceName.add("c02-000001");
        invoiceName.add("c01-000004");
        invoiceName.add("c01-000003");

        for(String x: invoiceName){
            System.out.println(x);
        }

    }


}
