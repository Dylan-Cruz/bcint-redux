package com.dragonslair.bcintredux.scryfall.enums;

import lombok.Getter;

@Getter
public enum Finish {
    foil("Foil", "F", "Foil"),
    nonfoil("Non-Foil", "R", "Normal"),
    etched("Etched Foil", "E", "Etched Foil"),
    glossy("Glossy", "G", "Glossy");

    private String name;
    private String skuCode;
    private String roca;

    Finish(String name, String skuCode, String roca) {
        this.name = name;
        this.skuCode = skuCode;
        this.roca = roca;
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

    public static Finish fromRoca(String roca) {
        for (Finish f : Finish.values()) {
            if (f.roca.equalsIgnoreCase(roca)) {
                return f;
            }
        }

        throw new IllegalArgumentException("No valid finish from roca string " + roca);
    }
}
