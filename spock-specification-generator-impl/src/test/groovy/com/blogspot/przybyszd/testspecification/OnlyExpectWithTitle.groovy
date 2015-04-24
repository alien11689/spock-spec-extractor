package com.blogspot.przybyszd.testspecification
import spock.lang.Specification
import spock.lang.Title

@Title("Tests of adding")
class OnlyExpectWithTitle extends Specification {
    def "adding test"() {
        expect:
            2 + 10 == 12
    }
}