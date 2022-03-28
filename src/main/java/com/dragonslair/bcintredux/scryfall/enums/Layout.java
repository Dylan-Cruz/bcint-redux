package com.dragonslair.bcintredux.scryfall.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Layout {
    normal,
    split,
    flip,
    transform,
    modal_dfc,
    meld,
    leveler,
    saga,
    adventure,
    planar,
    scheme,
    vanguard,
    token,
    double_faced_token,
    emblem,
    augment,
    host,
    art_series,
    reversible_card,
    @JsonProperty("class")
    class_enchantment
}