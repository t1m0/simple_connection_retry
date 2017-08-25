package com.t1m0.test;

public abstract class ANetworkAccess {

    protected final IConnectionProvider connectionProvider;
    protected final IRetryCache retryCache;

    protected ANetworkAccess(IConnectionProvider connectionProvider,IRetryCache retryCache){
        this.connectionProvider = connectionProvider;
        this.retryCache = retryCache;
    }

}
