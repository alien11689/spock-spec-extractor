package com.blogspot.przybyszd.spockspecificationgenerator.domain

import groovy.transform.Immutable

@Immutable
class Scenario {
    String name
    List<Statement> statements
    Set<String> issues
    Set<String> links

    boolean isValid() {
        !statements.isEmpty()
    }
}
