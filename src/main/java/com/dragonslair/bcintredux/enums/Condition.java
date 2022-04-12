package com.dragonslair.bcintredux.enums;

public enum Condition {
    NM("Near Mint"),
    LP("Lightly Played"),
    MP("Moderately Played"),
    HP("Heavily Played"),
    DG("Damaged");

    private String longForm;

    Condition (String longForm) {
        this.longForm = longForm;
    }

    public String getLongForm() {
        return longForm;
    }

    public static Condition fromLongForm(String s) {
        for (Condition c : Condition.values()) {
            if (s.startsWith(c.getLongForm())) {
                return c;
            }
        }
        throw new IllegalArgumentException("No condition exists for longform " + s);
    }

    public static Condition fromSku(String s) {
        for (Condition c : Condition.values()) {
            if (s.toUpperCase().endsWith(c.name())) {
                return c;
            }
        }
        return null;
    }
}

