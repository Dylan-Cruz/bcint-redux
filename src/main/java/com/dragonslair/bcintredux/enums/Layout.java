package com.dragonslair.bcintredux.enums;

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
    double_sided,
    @JsonProperty("class")
    class_enchantment
}
