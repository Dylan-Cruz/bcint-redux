package com.dragonslair.bcintredux.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Rarity {
    @JsonProperty("common")
    COMMON("Common"),
    @JsonProperty("uncommon")
    UNCOMMON("Uncommon"),
    @JsonProperty("rare")
    RARE("Rare"),
    @JsonProperty("mythic")
    MYTHIC("Mythic"),
    @JsonProperty("special")
    SPECIAL("Special");

    private String name;

    private Rarity(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
