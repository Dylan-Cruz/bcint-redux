package com.dragonslair.bcintredux.bigcommerce.enums;

public enum Categories {
    MAGICSINGLES(1065), MAGICBOOSTERS(22);

    private int id;

    private Categories(int id) {
        this.id = id;
    }

    public int getID() {
        return id;
    }
}