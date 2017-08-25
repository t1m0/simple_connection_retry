package com.t1m0.test;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;


public class FileRetryCache implements IRetryCache {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileRetryCache.class);

    private static final String BACKUP_FILE_NAME = "rest.backup";
    private static final String TMP_EXTENSION = ".tmp";

    private File backupFile = null;

    private File getFile(){
        if(backupFile == null){
            backupFile = new File(BACKUP_FILE_NAME);
        }
        return backupFile;
    }

    public void put(String msg){
        LOGGER.info("Writing message '"+msg+"' to '"+BACKUP_FILE_NAME+"'");
        try{
            FileUtils.writeStringToFile(getFile(),msg+"\n", "UTF-8");
        } catch (IOException e){
            LOGGER.error("Failed to write to backup file",e);
        }
    }

    public void processCachedItems(ItemProcessor itemProcessor){
        LineIterator lineIterator = null;
        File backupFile = new File(BACKUP_FILE_NAME);
        File tmpBackupFile = new File(BACKUP_FILE_NAME+TMP_EXTENSION);
        if(tmpBackupFile.exists())
            tmpBackupFile.delete();
        try{
            FileUtils.moveFile(backupFile,tmpBackupFile);
            lineIterator = FileUtils.lineIterator(tmpBackupFile, "UTF-8");
            while (lineIterator.hasNext()){
                String line = lineIterator.nextLine();
                itemProcessor.processCachedItem(line);
            }
        }catch (IOException e){
            LOGGER.error("Failed to read from backup file!",e);
        }finally {
            LineIterator.closeQuietly(lineIterator);
            tmpBackupFile.delete();
        }
    }
}
