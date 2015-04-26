package com.github.alien11689.spockspecextractor.core.test

import spock.lang.Specification

class OnlyExpectWithoutDescriptionSpec extends Specification {
    def "adding test"() {
        expect:
            2 + 10 == 12
    }
}