package com.t1m0.test;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class RerunRunnable extends ANetworkAccess implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(RerunRunnable.class);

    private static final long SLEEP_TIME = 10000;//10sec;
    private static final String TMP_EXTENSION = ".tmp";

    private static boolean running = false;

    public RerunRunnable(IConnectionProvider connectionProvider) {
        super(connectionProvider);
    }

    public static synchronized boolean isRunning() {
        return running;
    }

    private static void setRunning(boolean running) {
        RerunRunnable.running = running;
    }

    @Override
    public void run() {
        if(!isRunning()){
            setRunning(true);
            LOGGER.info("Start monitoring if connection is available again.");
            boolean httpConnectionBroken = true;
            do {
                httpConnectionBroken = connectionProvider.verifyConnection();
                if(!httpConnectionBroken){
                    LOGGER.info("Sleeping for '"+(SLEEP_TIME/1000)+"' seconds before the next retry.");
                    sleep(SLEEP_TIME);
                }
            } while (httpConnectionBroken);
            LOGGER.info("Connection established, starting rerun of failed messages.");
            Processor processor = new Processor(connectionProvider);
            File backupFile = new File(Processor.BACKUP_FILE_NAME);
            File tmpBackupFile = new File(Processor.BACKUP_FILE_NAME+TMP_EXTENSION);
            if(tmpBackupFile.exists())
                tmpBackupFile.delete();
            LineIterator iterator = null;
            try {
                while (!backupFile.canWrite()){
                    sleep(500);
                }
                FileUtils.moveFile(backupFile,tmpBackupFile);
                iterator = FileUtils.lineIterator(tmpBackupFile, "UTF-8");
                while (iterator.hasNext()) {
                    String line = iterator.nextLine();
                    processor.sendData(line);
                }
                iterator.close();
                tmpBackupFile.delete();
            } catch (Exception e){
                throw new RuntimeException("Error processing backup file, manual intervention required to process the left over messages",e);
            }finally {
                LineIterator.closeQuietly(iterator);
            }
            LOGGER.info("Rerun of failed messages finished, closing thread.");
            setRunning(false);
        } else {
            LOGGER.debug("Didn't launch a new thread as, there is already one running.");
        }
    }

    private void sleep(long time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            throw new RuntimeException("Sleep failed", e);
        }
    }
}

