package com.github.alien11689.spockspecextractor.core.domain

enum Block {
    EXPECT,
    CLEANUP,
    WHEN,
    THEN,
    SETUP,
    GIVEN,
    WHERE,
    AND;

    String capitalized() {
        "${name().charAt(0)}${name().substring(1).toLowerCase()}"
    }
}
