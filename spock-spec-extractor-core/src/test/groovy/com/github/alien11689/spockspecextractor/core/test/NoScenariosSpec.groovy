package com.github.alien11689.spockspecextractor.core.test

import spock.lang.Specification

class NoScenariosSpec extends Specification {
    String test() {
        ""
    }

    int size(int t) {
        t + 1
    }
}