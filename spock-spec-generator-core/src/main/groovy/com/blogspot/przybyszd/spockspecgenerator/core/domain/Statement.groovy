package com.blogspot.przybyszd.spockspecgenerator.core.domain

import groovy.transform.Immutable

@Immutable
class Statement {
    Block block
    String description
}
