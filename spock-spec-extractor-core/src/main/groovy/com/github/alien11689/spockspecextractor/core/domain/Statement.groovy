package com.github.alien11689.spockspecextractor.core.domain

import groovy.transform.Immutable

@Immutable
class Statement {
    Block block
    String description
}
