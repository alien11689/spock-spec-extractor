package com.blogspot.przybyszd.spockspecgenerator.it

import spock.lang.Issue
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

@Subject(Person)
class PersonTest extends Specification {
    @Unroll
    @Issue("http://mantis.com/123121")
    def "should test first and last name"() {
        given:
            Person p = new Person(firstName, lastName)
        expect:
            p.firstName == firstName
            p.lastName == lastName
        where:
            firstName | lastName
            "John"    | "Smith"
            "Tom"     | "Jones"
    }
}
