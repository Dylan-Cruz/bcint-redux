package com.dragonslair.bcintredux.bigcommerce;

public class BigCommerceServiceException extends RuntimeException {
    public BigCommerceServiceException(String message) {
        super(message);
    }

    public BigCommerceServiceException(String message, Exception e) {
        super(message, e);
    }
}
