package com.t1m0.test;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.time.format.DateTimeFormatter;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Created by timo.schoepflin on 23.08.2017.
 */
public class ProcessorTest {

    private static final String TEST_MESSAGE = "some message";

    private IConnectionProvider connectionProvider = mock(IConnectionProvider.class);
    private IRetryCache retryCache = mock(IRetryCache.class);

    @Test
    public void testFailedConnection() throws IConnectionProvider.ConnectionFailedException, InterruptedException {
        when(connectionProvider.sendData(anyString())).thenThrow(new IConnectionProvider.ConnectionFailedException("Uuups something failed!"));
        when(connectionProvider.verifyConnection()).thenReturn(false);
        Processor p = new Processor(connectionProvider,retryCache);
        p.sendData(TEST_MESSAGE);
        verify(retryCache).put(TEST_MESSAGE);
    }

    @Test
    @Ignore
    public void testSuccessfulRecovery() throws IConnectionProvider.ConnectionFailedException, InterruptedException {
        when(connectionProvider.sendData(anyString())).thenThrow(new IConnectionProvider.ConnectionFailedException("Uuups something failed!"));
        when(connectionProvider.verifyConnection()).thenReturn(true);
        doAnswer(invocation ->{return null;}).when(retryCache).put(TEST_MESSAGE);
        doAnswer(invocation -> {
            ((IRetryCache.ItemProcessor)invocation.getArguments()[1]).processCachedItem(TEST_MESSAGE);
            return null;
        }).when(retryCache).processCachedItems(any());
        Processor p = new Processor(connectionProvider,retryCache);
        p.sendData(TEST_MESSAGE);
        verify(retryCache).put(TEST_MESSAGE);
        verify(retryCache,timeout(500)).processCachedItems(any());
    }


}
