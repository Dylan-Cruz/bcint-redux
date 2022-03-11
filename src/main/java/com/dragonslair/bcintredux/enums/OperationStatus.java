package com.dragonslair.bcintredux.enums;

public enum OperationStatus {
    NOT_STARTED("Not Started"),
    IN_PROGRESS("In Progress"),
    ERRORED("Errored"),
    COMPLETED("Completed");

    private String label;

    OperationStatus(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
