package com.blogspot.przybyszd.spockspecgenerator.core.test

import spock.lang.Ignore
import spock.lang.Specification

@Ignore("Ignore spec description")
class WithIgnoredSpec extends Specification {
    @Ignore("ignored scenario")
    def "length of empty string is zero"() {
        expect:
            ''.length() == 0
    }

    @Ignore
    def "length should be positive"() {
        expect:
            "hi".length() > 0
    }
}