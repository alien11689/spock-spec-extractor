package com.blogspot.przybyszd.spockspecgenerator.core.domain

import groovy.transform.Immutable

@Immutable
class Spec {
    String name
    String title
    String description
    Set<Class> subjects
    List<Scenario> scenarios = []
    Set<String> issues
    Set<String> links
    Ignored ignored

    boolean isValid() {
        !scenarios.isEmpty()
    }
}



