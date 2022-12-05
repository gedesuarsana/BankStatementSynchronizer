package com.brinks.services;

import org.apache.commons.net.ftp.FTPClient;

public interface FTPService {

    public FTPClient loginFtp() throws Exception;
    public void printTree(String path, FTPClient ftpClient) throws Exception;
    public byte[] downloadFile(String path, FTPClient ftpClient) throws Exception;
}
