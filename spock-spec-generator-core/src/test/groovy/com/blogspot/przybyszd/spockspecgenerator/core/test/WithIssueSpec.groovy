package com.blogspot.przybyszd.spockspecgenerator.core.test

import spock.lang.Issue
import spock.lang.Specification

@Issue(['http://mantis.test/1', 'http://bugzilla.test/145'])
class WithIssueSpec extends Specification {
    @Issue('http://mantis.test/5')
    def "length of empty string is zero"() {
        expect:
            ''.length() == 0
    }

    @Issue(['http://mantis.test/132', 'http://bugzilla.test/1212'])
    def "length should be positive"() {
        expect:
            "hi".length() > 0
    }
}