package com.blogspot.przybyszd.spockspecificationgenerator.domain

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

    boolean isValid() {
        !scenarios.isEmpty()
    }
}



