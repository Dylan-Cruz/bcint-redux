package com.dragonslair.bcintredux.enums;

public enum Condition {
    NM("Near Mint"),
    PL("Played"),
    LP("Lightly Played"),
    MP("Moderately Played"),
    HP("Heavily Played"),
    DMG("Damaged");

    private String longForm;

    Condition (String longForm) {
        this.longForm = longForm;
    }

    public String getLongForm() {
        return longForm;
    }
}

