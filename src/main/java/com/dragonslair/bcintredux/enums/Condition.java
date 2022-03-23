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

    public static Condition fromLongForm(String s) {
        for (Condition c : Condition.values()) {
            if (s.toLowerCase().startsWith(c.getLongForm())) {
                return c;
            }
        }
        return null;
    }
}

