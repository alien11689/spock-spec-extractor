package com.blogspot.przybyszd.testspecification

import spock.lang.Specification
import spock.lang.Subject

@Subject([String, Long])
class OnlyExpectWithSubject extends Specification {

    String testField = ""

    def "adding test"() {
        expect:
            2 + 10 == 12
    }

    @Subject(Byte)
    Integer testField2 = 5

    @Subject
    int testField3 = 6
}