package com.brinks.services.impl;

import com.brinks.models.Transaction;
import com.brinks.repository.TransactionRepository;
import com.brinks.services.FTPService;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;


@Service("FTPService")
public class FTPServiceImpl implements FTPService {

    @Value("${ftp.host}")
    private String host;
    @Value("${ftp.port}")
    private int port;
    @Value("${ftp.username}")
    private String username;
    @Value("${ftp.password}")
    private String password;



    @Override
    public FTPClient loginFtp() throws Exception {
        FTPClient ftpClient = new FTPClient();
        ftpClient.connect(host, port);
        ftpClient.login(username, password);
        System.out.println("FTP Connect to host:"+host+" port:"+port+" username:"+username+" password:"+password);
        return ftpClient;
    }
    @Override
    public void printTree(String path, FTPClient ftpClient) throws Exception {
        for (FTPFile ftpFile : ftpClient.listFiles(path)) {
            System.out.println();
            System.out.printf("[printTree][%d]\n", System.currentTimeMillis());
            System.out.printf("[printTree][%d] Get name : %s \n", System.currentTimeMillis(), ftpFile.getName());
            System.out.printf("[printTree][%d] Get timestamp : %s \n", System.currentTimeMillis(), ftpFile.getTimestamp().getTimeInMillis());
            System.out.printf("[printTree][%d] Get group : %s \n", System.currentTimeMillis(), ftpFile.getGroup());
            System.out.printf("[printTree][%d] Get link : %s \n", System.currentTimeMillis(), ftpFile.getLink());
            System.out.printf("[printTree][%d] Get user : %s \n", System.currentTimeMillis(), ftpFile.getUser());
            System.out.printf("[printTree][%d] Get type : %s \n", System.currentTimeMillis(), ftpFile.getType());
            System.out.printf("[printTree][%d] Is file : %s \n", System.currentTimeMillis(), ftpFile.isFile());
            System.out.printf("[printTree][%d] Is directory : %s \n", System.currentTimeMillis(), ftpFile.isDirectory());
            System.out.printf("[printTree][%d] Formatted string : %s \n", System.currentTimeMillis(), ftpFile.toFormattedString());
            System.out.println();

            if (ftpFile.isDirectory()) {
                printTree(path + File.separator + ftpFile.getName(), ftpClient);
            }
        }
    }
    @Override
    public byte[] downloadFile(TransactionRepository transactionRepository, Transaction transaction, String path, FTPClient ftpClient) throws Exception {
        FTPFile firstFileToDownload=null;


       for(FTPFile ftpFile: ftpClient.listFiles(path)){
           System.out.println("ftp item:"+ftpFile.getName());
           if(ftpFile.isFile()){
               firstFileToDownload=ftpFile;
               break;
           }
       }

        String fullPath = path+"/"+firstFileToDownload.getName();

       Transaction previousTransaction = transactionRepository.findByFileName(firstFileToDownload.getName());

       if(previousTransaction!=null){
           return null;
       }


        transaction.setFileName(firstFileToDownload.getName());


        //ftpClient.enterLocalPassiveMode();
        // ftpClient.doCommand("CWD","/");
       // ftpClient.doCommand("TYPE","A");
       // ftpClient.doCommand("EPSV",null);




        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        System.out.println();
        System.out.printf("[downloadFile][%d] Is success to download file : %s -> %b",
                System.currentTimeMillis(), fullPath, ftpClient.retrieveFile(fullPath, byteArrayOutputStream));
        System.out.println();

        return byteArrayOutputStream.toByteArray();

    }


}
