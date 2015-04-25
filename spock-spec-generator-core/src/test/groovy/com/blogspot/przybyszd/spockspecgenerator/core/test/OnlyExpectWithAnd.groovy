package com.blogspot.przybyszd.spockspecgenerator.core.test
import spock.lang.Specification
import spock.lang.Title

class OnlyExpectWithAnd extends Specification {
    def "adding test"() {
        expect:
            2 + 10 == 12
        and:
            12 > 0
    }
}