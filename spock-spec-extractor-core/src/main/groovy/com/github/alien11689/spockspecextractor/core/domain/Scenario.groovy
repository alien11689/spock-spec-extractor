package com.github.alien11689.spockspecextractor.core.domain

import groovy.transform.Immutable

@Immutable
class Scenario {
    String name
    List<Statement> statements
    Set<String> issues
    Set<String> links
    Ignored ignored

    boolean isValid() {
        !statements.isEmpty()
    }
}
