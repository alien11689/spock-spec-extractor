package com.blogspot.przybyszd.spockspecgenerator.core

import com.blogspot.przybyszd.spockspecgenerator.core.domain.Block
import com.blogspot.przybyszd.spockspecgenerator.core.domain.Scenario
import com.blogspot.przybyszd.spockspecgenerator.core.domain.Spec
import com.blogspot.przybyszd.spockspecgenerator.core.domain.Statement
import spock.lang.Specification

class SpockSpecGeneratorTest extends Specification {

    SpockSpecGenerator sut = new SpockSpecGenerator()

    def "should generate spec for only expect with description spec"() {
        given:
            String code = this.class.getResource("/com/blogspot/przybyszd/spockspecgenerator/core/test/OnlyExpectWithDescriptionSpec.groovy").text
        when:
            List<Spec> specs = sut.generateSpec(code, getClass().classLoader)
        then:
            specs.size() == 1
            specs[0] == new Spec(name: "com.blogspot.przybyszd.spockspecgenerator.core.test.OnlyExpectWithDescriptionSpec", scenarios: [
                    new Scenario(name: "adding test", statements: [new Statement(block: Block.EXPECT, description: "adding works")])
            ])
    }

    def "should generate spec for only expect without description spec"() {
        given:
            String code = this.class.getResource("/com/blogspot/przybyszd/spockspecgenerator/core/test/OnlyExpectWithoutDescriptionSpec.groovy").text
        when:
            List<Spec> specs = sut.generateSpec(code, getClass().classLoader)
        then:
            specs.size() == 1
            specs[0] == new Spec(name: "com.blogspot.przybyszd.spockspecgenerator.core.test.OnlyExpectWithoutDescriptionSpec", scenarios: [
                    new Scenario(name: "adding test", statements: [new Statement(block: Block.EXPECT)])
            ])
    }

    def "should generate spec with when then with description spec"() {
        given:
            String code = this.class.getResource("/com/blogspot/przybyszd/spockspecgenerator/core/test/WhenThenWithDescriptionSpec.groovy").text
        when:
            List<Spec> specs = sut.generateSpec(code, getClass().classLoader)
        then:
            specs.size() == 1
            specs[0] == new Spec(name: "com.blogspot.przybyszd.spockspecgenerator.core.test.WhenThenWithDescriptionSpec", scenarios: [
                    new Scenario(name: "length should be positive", statements: [
                            new Statement(block: Block.WHEN, description: "get length of string"),
                            new Statement(block: Block.THEN, description: "length is positive")
                    ])
            ])
    }

    def "should generate spec with where spec"() {
        given:
            String code = this.class.getResource("/com/blogspot/przybyszd/spockspecgenerator/core/test/WithWhereSpec.groovy").text
        when:
            List<Spec> specs = sut.generateSpec(code, getClass().classLoader)
        then:
            specs.size() == 1
            specs[0] == new Spec(name: "com.blogspot.przybyszd.spockspecgenerator.core.test.WithWhereSpec", scenarios: [
                    new Scenario(name: "length should be positive", statements: [
                            new Statement(block: Block.WHEN),
                            new Statement(block: Block.THEN),
                            new Statement(block: Block.WHERE)
                    ])
            ])
    }

    def "should generate spec with given expect and setup expect cleanup"() {
        given:
            String code = this.class.getResource("/com/blogspot/przybyszd/spockspecgenerator/core/test/GivenExpectAndSetupExpectCleanupSpec.groovy").text
        when:
            List<Spec> specs = sut.generateSpec(code, getClass().classLoader)
        then:
            specs.size() == 1
            specs[0] == new Spec(name: "com.blogspot.przybyszd.spockspecgenerator.core.test.GivenExpectAndSetupExpectCleanupSpec",
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
            String code = this.class.getResource("/com/blogspot/przybyszd/spockspecgenerator/core/test/WithNoBlocksSpec.groovy").text
        when:
            List<Spec> specs = sut.generateSpec(code, getClass().classLoader)
        then:
            specs.size() == 1
            specs[0] == new Spec(name: "com.blogspot.przybyszd.spockspecgenerator.core.test.WithNoBlocksSpec", scenarios: [
                    new Scenario(name: "adding test", statements: [new Statement(block: Block.EXPECT, description: "adding works")])
            ])
    }

    def "should generate empty spec when there are no scenarios"() {
        given:
            String code = this.class.getResource("/com/blogspot/przybyszd/spockspecgenerator/core/test/NoScenariosSpec.groovy").text
        when:
            List<Spec> specs = sut.generateSpec(code, getClass().classLoader)
        then:
            specs.isEmpty()
    }

    def "should generate spec for only expect with Title annotation"() {
        given:
            String code = this.class.getResource("/com/blogspot/przybyszd/spockspecgenerator/core/test/OnlyExpectWithTitle.groovy").text
        when:
            List<Spec> specs = sut.generateSpec(code, getClass().classLoader)
        then:
            specs.size() == 1
            specs[0] == new Spec(
                    name: "com.blogspot.przybyszd.spockspecgenerator.core.test.OnlyExpectWithTitle",
                    title: "Tests of adding",
                    scenarios: [
                            new Scenario(
                                    name: "adding test",
                                    statements: [
                                            new Statement(
                                                    block: Block.EXPECT)])
                    ])
    }

    def "should generate spec for only expect with and"() {
        given:
            String code = this.class.getResource("/com/blogspot/przybyszd/spockspecgenerator/core/test/OnlyExpectWithAnd.groovy").text
        when:
            List<Spec> specs = sut.generateSpec(code, getClass().classLoader)
        then:
            specs.size() == 1
            specs[0] == new Spec(
                    name: "com.blogspot.przybyszd.spockspecgenerator.core.test.OnlyExpectWithAnd",
                    scenarios: [
                            new Scenario(
                                    name: "adding test",
                                    statements: [
                                            new Statement(block: Block.EXPECT),
                                            new Statement(block: Block.AND)
                                    ])
                    ])
    }

    def "should generate spec for only expect with Narrative annotation"() {
        given:
            String code = this.class.getResource("/com/blogspot/przybyszd/spockspecgenerator/core/test/OnlyExpectWithNarrative.groovy").text
        when:
            List<Spec> specs = sut.generateSpec(code, getClass().classLoader)
        then:
            specs.size() == 1
            specs[0] == new Spec(
                    name: "com.blogspot.przybyszd.spockspecgenerator.core.test.OnlyExpectWithNarrative",
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
            String code = this.class.getResource("/com/blogspot/przybyszd/spockspecgenerator/core/test/OnlyExpectWithSubject.groovy").text
        when:
            List<Spec> specs = sut.generateSpec(code, getClass().classLoader)
        then:
            specs.size() == 1
            specs[0] == new Spec(
                    name: "com.blogspot.przybyszd.spockspecgenerator.core.test.OnlyExpectWithSubject",
                    subjects: [String, Long, int, Byte] as Set,
                    scenarios: [
                            new Scenario(
                                    name: "adding test",
                                    statements: [
                                            new Statement(
                                                    block: Block.EXPECT)])
                    ])
    }

    def "should generate spec with See annotation on class and methods"() {
        given:
            String code = this.class.getResource("/com/blogspot/przybyszd/spockspecgenerator/core/test/WithSeeSpec.groovy").text
        when:
            List<Spec> specs = sut.generateSpec(code, getClass().classLoader)
        then:
            specs.size() == 1
            specs[0] == new Spec(
                    name: "com.blogspot.przybyszd.spockspecgenerator.core.test.WithSeeSpec",
                    links: ['http://google.com', 'http://oracle.com'] as Set,
                    scenarios: [
                            new Scenario(
                                    name: "length of empty string is zero",
                                    links: ['http://test.com'] as Set,
                                    statements: [
                                            new Statement(
                                                    block: Block.EXPECT)]),
                            new Scenario(
                                    name: "length should be positive",
                                    links: ['http://length.com', 'http://hi.com'] as Set,
                                    statements: [
                                            new Statement(
                                                    block: Block.EXPECT),
                                            new Statement(
                                                    block: Block.WHERE)])
                    ])
    }

    def "should generate spec with Issue annotation on class and methods"() {
        given:
            String code = this.class.getResource("/com/blogspot/przybyszd/spockspecgenerator/core/test/WithIssueSpec.groovy").text
        when:
            List<Spec> specs = sut.generateSpec(code, getClass().classLoader)
        then:
            specs.size() == 1
            specs[0] == new Spec(
                    name: "com.blogspot.przybyszd.spockspecgenerator.core.test.WithIssueSpec",
                    issues: ['http://mantis.test/1', 'http://bugzilla.test/145'] as Set,
                    scenarios: [
                            new Scenario(
                                    name: "length of empty string is zero",
                                    issues: ['http://mantis.test/5'] as Set,
                                    statements: [
                                            new Statement(
                                                    block: Block.EXPECT)]),
                            new Scenario(
                                    name: "length should be positive",
                                    issues: ['http://mantis.test/132', 'http://bugzilla.test/1212'] as Set,
                                    statements: [
                                            new Statement(
                                                    block: Block.EXPECT)])
                    ])
    }
}