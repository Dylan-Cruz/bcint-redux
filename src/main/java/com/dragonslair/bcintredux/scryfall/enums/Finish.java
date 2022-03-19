package com.dragonslair.bcintredux.scryfall.enums;

import lombok.Getter;

@Getter
public enum Finish {
    foil("Foil", "F"),
    nonfoil("Non-Foil", "R"),
    etched("Etched Foil", "E"),
    glossy("Glossy", "G");

    private String name;
    private String skuCode;

    Finish(String name, String skuCode) {
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
