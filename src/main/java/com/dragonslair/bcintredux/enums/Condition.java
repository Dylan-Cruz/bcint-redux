package com.dragonslair.bcintredux.enums;

public enum Condition {
    NM("Near Mint"),
    PL("Played");

    private String longForm;

    private Condition (String longForm) {
        this.longForm = longForm;
    }

    public String getLongForm() {
        return longForm;
    }
}