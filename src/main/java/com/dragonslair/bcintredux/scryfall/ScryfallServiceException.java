package com.dragonslair.bcintredux.scryfall;

public class ScryfallServiceException extends Exception {

    public ScryfallServiceException(String message, Exception e) {
        super(message, e);
    }
}
