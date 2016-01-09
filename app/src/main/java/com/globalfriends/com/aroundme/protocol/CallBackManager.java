package com.globalfriends.com.aroundme.protocol;

import java.util.HashSet;

/**
 * Created by Vishal on 1/8/2016.
 */
public class CallBackManager {
    private HashSet<TransactionManager.Result> mListeners = new HashSet<>();

    /**
     * @param result
     */
    public void addResultCallback(final TransactionManager.Result result) {
        synchronized (mListeners) {
            mListeners.add(result);
        }
    }

    /**
     * @param result
     */
    public void removeResultCallback(final TransactionManager.Result result) {
        synchronized (mListeners) {
            mListeners.remove(result);
        }
    }

    public HashSet<TransactionManager.Result> getListeners() {
        synchronized (mListeners) {
            return (HashSet<TransactionManager.Result>) mListeners.clone();
        }
    }
}
