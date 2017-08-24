package com.t1m0.test;

public abstract class ANetworkAccess {

    protected final IConnectionProvider connectionProvider;

    protected ANetworkAccess(IConnectionProvider connectionProvider){
        this.connectionProvider = connectionProvider;
    }

}
