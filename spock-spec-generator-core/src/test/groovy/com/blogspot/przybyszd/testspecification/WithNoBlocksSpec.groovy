package com.blogspot.przybyszd.testspecification
import spock.lang.Specification

class WithNoBlocksSpec extends Specification {
    String test (){
        ""
    }

    def "adding test"() {
        expect: "adding works"
            2 + 10 == 12
    }

    int size(int t){
        t+1
    }
}