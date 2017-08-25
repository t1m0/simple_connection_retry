package com.t1m0.test;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Iterator;

/** Launched by the processor in case of any connectivity errors and retries to send the failed messages if the connection is established again. */
public class RetryRunnable extends ANetworkAccess implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(RetryRunnable.class);

    private static final long SLEEP_TIME = 10000;//10sec;


    private static boolean running = false;

    public RetryRunnable(IConnectionProvider connectionProvider, IRetryCache retryCache) {
        super(connectionProvider,retryCache);
    }

    public static synchronized boolean isRunning() {
        return running;
    }

    private static void setRunning(boolean running) {
        RetryRunnable.running = running;
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
            Processor processor = new Processor(connectionProvider,retryCache);
            retryCache.processCachedItems((message)-> processor.sendData(message));
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

