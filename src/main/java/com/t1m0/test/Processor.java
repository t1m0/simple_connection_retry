package com.t1m0.test;

/** Some processor sending message over the network */
public class Processor extends ANetworkAccess {

    protected Processor(IConnectionProvider connectionProvider, IRetryCache retryCache) {
        super(connectionProvider,retryCache);
    }

    private void HandleConnectionError(String jsonMsg){
        retryCache.put(jsonMsg);
        new Thread(new RetryRunnable(connectionProvider,retryCache)).start();
    }

    public void sendData(String msg) {
        try {
            connectionProvider.sendData(msg);
        }catch (IConnectionProvider.ConnectionFailedException e){
            HandleConnectionError(msg);
        }
    }

}
