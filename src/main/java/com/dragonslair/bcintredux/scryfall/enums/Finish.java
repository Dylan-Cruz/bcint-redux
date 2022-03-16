package com.dragonslair.bcintredux.scryfall.enums;

import lombok.Getter;

@Getter
public enum Finish {
    foil("Foil", "F"),
    nonfoil("Regular", "R"),
    etched("Etched", "E"),
    glossy("Glossy", "G");

    private String name;
    private String skuCode;

    private Finish(String name, String skuCode) {
        this.name = name;
        this.skuCode = skuCode;
    }

    public static Finish fromSkuCode(String skuCode) {
        return switch (skuCode) {
            case "F" -> foil;
            case "R" -> nonfoil;
            case "E" -> etched;
            case "G" -> glossy;
            default -> throw new IllegalArgumentException("No corresponding finish for variant sku " + skuCode);
        };
    }
}
