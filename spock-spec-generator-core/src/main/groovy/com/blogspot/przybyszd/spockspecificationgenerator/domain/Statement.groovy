package com.blogspot.przybyszd.spockspecificationgenerator.domain

import groovy.transform.Immutable

@Immutable
class Statement {
    Block block
    String description
}
