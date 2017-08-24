package com.t1m0.test;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;

public class Processor extends ANetworkAccess {

    public static final String BACKUP_FILE_NAME = "rest.backup";

    private static final Logger LOGGER = LoggerFactory.getLogger(Processor.class);

    private File backupFile = null;

    protected Processor(IConnectionProvider connectionProvider) {
        super(connectionProvider);
    }

    private File getFile(){
        if(backupFile == null){
            backupFile = new File(BACKUP_FILE_NAME);
        }
        return backupFile;
    }

    private void HandleConnectionError(String jsonMsg){
        try{
            FileUtils.writeStringToFile(getFile(),jsonMsg+"\n", Charset.forName("UTF-8"));
        } catch (IOException e){
            LOGGER.error("Failed to write to backup file",e);
        }
        new Thread(new RerunRunnable(connectionProvider)).start();
    }

    public void sendData(String msg) {
        try {
            connectionProvider.sendData(msg);
        }catch (IConnectionProvider.ConnectionFailedException e){
            LOGGER.info("Writing message '"+msg+"' to '"+BACKUP_FILE_NAME+"'");
            HandleConnectionError(msg);
        }
    }

}
