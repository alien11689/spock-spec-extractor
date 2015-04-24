package com.blogspot.przybyszd.testspecification

import spock.lang.Specification

class OnlyExpectWithoutDescriptionSpec extends Specification {
    def "adding test"() {
        expect:
            2 + 10 == 12
    }
}