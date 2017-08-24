package com.t1m0.test;

import org.junit.Test;
import org.mockito.Mock;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by timo.schoepflin on 23.08.2017.
 */
public class ProcessorTest {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    private static final String MSG_FORMAT = "{\"timestamp\":\"%s\",\"message\":\"%s\"}";

    private IConnectionProvider connectionProvider = mock(IConnectionProvider.class);

    @Test
    public void testFailedConnection() throws IConnectionProvider.ConnectionFailedException, InterruptedException {
        when(connectionProvider.sendData(anyString())).thenThrow(new IConnectionProvider.ConnectionFailedException("Uuups something failed!"));
        when(connectionProvider.verifyConnection()).thenReturn(false);
        Processor p = new Processor(connectionProvider);
        p.sendData("some message");
        Thread.sleep(50);
        assertTrue(RerunRunnable.isRunning());
    }


}
