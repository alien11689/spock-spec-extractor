package com.blogspot.przybyszd.testspecification

import spock.lang.Narrative
import spock.lang.Specification

@Narrative("Long description of tests")
class OnlyExpectWithNarrative extends Specification {
    def "adding test"() {
        expect:
            2 + 10 == 12
    }
}