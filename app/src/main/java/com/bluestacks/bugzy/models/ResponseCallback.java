package com.bluestacks.bugzy.models;


public interface ResponseCallback<T extends Response> {
    void onError(int errorCode, String message);

    void onSuccess();
    // Success means
}


/**
 * Type of error:
 * 1. Network Error: 4xx
 * 2. Server Error : 5xx
 * 3. API Error
 */

