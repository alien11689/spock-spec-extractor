package com.blogspot.przybyszd.spockspecgenerator.it

import spock.lang.Ignore
import spock.lang.Issue
import spock.lang.Narrative
import spock.lang.See
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Title
import spock.lang.Unroll

@Subject(Person)
@Title("This is specification for Person")
@See(["http://google.com", 'http://amazon.com'])
@Issue(["http://mantis.com/123121","http://mantis.com/123122"])
@Narrative("We are testing if constructor of Person class works well")
class PersonTest extends Specification {
    @Unroll
    @See(["http://google2.com", 'http://amazon2.com'])
    @Issue(["http://mantis.com/5","http://mantis.com/2"])
    def "should test first and last name"() {
        given: "person created with first and last name"
            Person p = new Person(firstName, lastName)
        expect: "first name is equal given"
            p.firstName == firstName
        and: "last name is equal given"
            p.lastName == lastName
        where:
            firstName | lastName
            "John"    | "Smith"
            "Tom"     | "Jones"
    }

    @Ignore("Short ignore description")
    def "ignore this test"(){
        expect:
            1 == 1
    }
}
