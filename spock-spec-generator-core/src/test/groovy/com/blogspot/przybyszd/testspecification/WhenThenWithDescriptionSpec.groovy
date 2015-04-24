package com.blogspot.przybyszd.testspecification
import spock.lang.Specification

class WhenThenWithDescriptionSpec extends Specification {
    def "length should be positive"() {
        when: "get length of string"
            int length = "request".length()
        then: "length is positive"
            length > 0
    }
}