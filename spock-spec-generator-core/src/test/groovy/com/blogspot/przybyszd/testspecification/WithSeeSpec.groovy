package com.blogspot.przybyszd.testspecification

import spock.lang.See
import spock.lang.Specification
import spock.lang.Unroll

@See(['http://google.com', 'http://oracle.com'])
class WithSeeSpec extends Specification {
    @See('http://test.com')
    def "length of empty string is zero"() {
        expect:
            ''.length() == 0
    }

    @See(['http://length.com', 'http://hi.com'])
    @Unroll
    def "length should be positive"() {
        expect:
            "hi".length() > 0
        where:
            a << [1,2]
    }

    def "util method"(){

    }
}