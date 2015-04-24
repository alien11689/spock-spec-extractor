package com.blogspot.przybyszd.spockspecificationgenerator

import com.blogspot.przybyszd.spockspecificationgenerator.domain.Block
import com.blogspot.przybyszd.spockspecificationgenerator.domain.Scenario
import com.blogspot.przybyszd.spockspecificationgenerator.domain.Spec
import com.blogspot.przybyszd.spockspecificationgenerator.domain.Statement
import spock.lang.Specification

class SpockSpecGeneratorTest extends Specification {

    SpockSpecGenerator sut = new SpockSpecGenerator()

    def "should generate spec for only expect with description spec"() {
        given:
            String code = this.class.getResource("/com/blogspot/przybyszd/testspecification/OnlyExpectWithDescriptionSpec.groovy").text
        when:
            List<Spec> specs = sut.generateSpec(code)
        then:
            specs.size() == 1
            specs[0] == new Spec(name: "com.blogspot.przybyszd.testspecification.OnlyExpectWithDescriptionSpec", scenarios: [
                    new Scenario(name: "adding test", statements: [new Statement(block: Block.EXPECT, description: "adding works")])
            ])
    }

    def "should generate spec for only expect without description spec"() {
        given:
            String code = this.class.getResource("/com/blogspot/przybyszd/testspecification/OnlyExpectWithoutDescriptionSpec.groovy").text
        when:
            List<Spec> specs = sut.generateSpec(code)
        then:
            specs.size() == 1
            specs[0] == new Spec(name: "com.blogspot.przybyszd.testspecification.OnlyExpectWithoutDescriptionSpec", scenarios: [
                    new Scenario(name: "adding test", statements: [new Statement(block: Block.EXPECT)])
            ])
    }

    def "should generate spec with when then with description spec"() {
        given:
            String code = this.class.getResource("/com/blogspot/przybyszd/testspecification/WhenThenWithDescriptionSpec.groovy").text
        when:
            List<Spec> specs = sut.generateSpec(code)
        then:
            specs.size() == 1
            specs[0] == new Spec(name: "com.blogspot.przybyszd.testspecification.WhenThenWithDescriptionSpec", scenarios: [
                    new Scenario(name: "length should be positive", statements: [
                            new Statement(block: Block.WHEN, description: "get length of string"),
                            new Statement(block: Block.THEN, description: "length is positive")
                    ])
            ])
    }

    def "should generate spec with where spec"() {
        given:
            String code = this.class.getResource("/com/blogspot/przybyszd/testspecification/WithWhereSpec.groovy").text
        when:
            List<Spec> specs = sut.generateSpec(code)
        then:
            specs.size() == 1
            specs[0] == new Spec(name: "com.blogspot.przybyszd.testspecification.WithWhereSpec", scenarios: [
                    new Scenario(name: "length should be positive", statements: [
                            new Statement(block: Block.WHEN),
                            new Statement(block: Block.THEN),
                            new Statement(block: Block.WHERE)
                    ])
            ])
    }

    def "should generate spec with given expect and setup expect cleanup"() {
        given:
            String code = this.class.getResource("/com/blogspot/przybyszd/testspecification/GivenExpectAndSetupExpectCleanupSpec.groovy").text
        when:
            List<Spec> specs = sut.generateSpec(code)
        then:
            specs.size() == 1
            specs[0] == new Spec(name: "com.blogspot.przybyszd.testspecification.GivenExpectAndSetupExpectCleanupSpec",
                    scenarios: [
                            new Scenario(name: "length should be positive", statements: [
                                    new Statement(block: Block.GIVEN, description: "A string"),
                                    new Statement(block: Block.EXPECT, description: "check length")
                            ]),
                            new Scenario(name: "length should be positive 2", statements: [
                                    new Statement(block: Block.SETUP, description: "A string"),
                                    new Statement(block: Block.EXPECT, description: "check length"),
                                    new Statement(block: Block.CLEANUP, description: "Clean after test")
                            ])
                    ])
    }

    def "should generate spec without scenario when there is no blocks in method"() {
        given:
            String code = this.class.getResource("/com/blogspot/przybyszd/testspecification/WithNoBlocksSpec.groovy").text
        when:
            List<Spec> specs = sut.generateSpec(code)
        then:
            specs.size() == 1
            specs[0] == new Spec(name: "com.blogspot.przybyszd.testspecification.WithNoBlocksSpec", scenarios: [
                    new Scenario(name: "adding test", statements: [new Statement(block: Block.EXPECT, description: "adding works")])
            ])
    }

    def "should generate empty spec when there are no scenarios"() {
        given:
            String code = this.class.getResource("/com/blogspot/przybyszd/testspecification/NoScenariosSpec.groovy").text
        when:
            List<Spec> specs = sut.generateSpec(code)
        then:
            specs.isEmpty()
    }

    def "should generate spec for only expect with Title annotation"() {
        given:
            String code = this.class.getResource("/com/blogspot/przybyszd/testspecification/OnlyExpectWithTitle.groovy").text
        when:
            List<Spec> specs = sut.generateSpec(code)
        then:
            specs.size() == 1
            specs[0] == new Spec(
                    name: "com.blogspot.przybyszd.testspecification.OnlyExpectWithTitle",
                    title: "Tests of adding",
                    scenarios: [
                            new Scenario(
                                    name: "adding test",
                                    statements: [
                                            new Statement(
                                                    block: Block.EXPECT)])
                    ])
    }

    def "should generate spec for only expect with Narrative annotation"() {
        given:
            String code = this.class.getResource("/com/blogspot/przybyszd/testspecification/OnlyExpectWithNarrative.groovy").text
        when:
            List<Spec> specs = sut.generateSpec(code)
        then:
            specs.size() == 1
            specs[0] == new Spec(
                    name: "com.blogspot.przybyszd.testspecification.OnlyExpectWithNarrative",
                    description: "Long description of tests",
                    scenarios: [
                            new Scenario(
                                    name: "adding test",
                                    statements: [
                                            new Statement(
                                                    block: Block.EXPECT)])
                    ])
    }

    def "should generate spec Subject annotation on class and field"() {
        given:
            String code = this.class.getResource("/com/blogspot/przybyszd/testspecification/OnlyExpectWithSubject.groovy").text
        when:
            List<Spec> specs = sut.generateSpec(code)
        then:
            specs.size() == 1
            specs[0] == new Spec(
                    name: "com.blogspot.przybyszd.testspecification.OnlyExpectWithSubject",
                    subjects: [String, Long, int, Byte] as Set,
                    scenarios: [
                            new Scenario(
                                    name: "adding test",
                                    statements: [
                                            new Statement(
                                                    block: Block.EXPECT)])
                    ])
    }
}