package com.blogspot.przybyszd.spockspecificationgenerator.domain

import groovy.transform.Immutable

@Immutable
class Scenario {
    String name
    List<Statement> statements

    boolean isValid() {
        !statements.isEmpty()
    }
}
