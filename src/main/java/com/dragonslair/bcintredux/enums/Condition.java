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

// nm 100
// lp 90
// mp 75
// hp 60
// dmg 50