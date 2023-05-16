package com.brinks.services;

import com.brinks.models.Transaction;
import com.brinks.repository.TransactionRepository;
import org.apache.commons.net.ftp.FTPClient;

public interface FTPService {

    public FTPClient loginFtp() throws Exception;
    public void printTree(String path, FTPClient ftpClient) throws Exception;
    public byte[] downloadFile(TransactionRepository transactionRepository,Transaction transaction, String path, FTPClient ftpClient) throws Exception;
}
