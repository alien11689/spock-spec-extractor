package com.blogspot.przybyszd.spockspecgenerator.core.test
import spock.lang.Specification

class WithWhereSpec extends Specification {
    def "length should be positive"() {
        when:
            int length = "request".length()
        then:
            length > 0
        where:
            a << ['1', '2', '3']
    }
}