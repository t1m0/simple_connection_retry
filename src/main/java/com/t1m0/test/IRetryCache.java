package com.t1m0.test;

/** The IRetryCache interface provides an abstraction to implement different caching solutions. */
public interface IRetryCache {
    void put(String msg);
    void processCachedItems(ItemProcessor itemProcessor);

    interface ItemProcessor{
        void processCachedItem(String item);
    }
}
