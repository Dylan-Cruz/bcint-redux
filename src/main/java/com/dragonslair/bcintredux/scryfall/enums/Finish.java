package com.dragonslair.bcintredux.scryfall.enums;

import lombok.Getter;

@Getter
public enum Finish {
    foil("Foil", "F"),
    nonfoil("Regular", "R"),
    etched("Etched", "E"),
    glossy("Glossy", "G");

    private String name;
    private String skuValue;

    private Finish(String name, String skuValue) {
        this.name = name;
        this.skuValue = skuValue;
    }
}
