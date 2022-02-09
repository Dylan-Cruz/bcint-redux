package com.dragonslair.bcintredux.scryfall;

public class ScryfallServiceException extends RuntimeException {

    public ScryfallServiceException(String message) {
        super(message);
    }

    public ScryfallServiceException(String message, Exception e) {
        super(message, e);
    }
}
