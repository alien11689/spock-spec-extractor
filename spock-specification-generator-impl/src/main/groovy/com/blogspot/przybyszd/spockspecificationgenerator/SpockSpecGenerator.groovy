package com.blogspot.przybyszd.spockspecificationgenerator

import com.blogspot.przybyszd.spockspecificationgenerator.domain.Block
import com.blogspot.przybyszd.spockspecificationgenerator.domain.Scenario
import com.blogspot.przybyszd.spockspecificationgenerator.domain.Spec
import com.blogspot.przybyszd.spockspecificationgenerator.domain.Statement
import groovyjarjarantlr.collections.AST
import org.codehaus.groovy.antlr.parser.GroovyLexer
import org.codehaus.groovy.antlr.parser.GroovyRecognizer
import org.codehaus.groovy.antlr.parser.GroovyTokenTypes
import spock.lang.Narrative
import spock.lang.Subject
import spock.lang.Title

import java.lang.reflect.Field

class SpockSpecGenerator {
    List<Spec> generateSpec(String code) {
        AST ast = createAST(code)
        return getSpecs(ast)
    }

    private static List<Spec> getSpecs(AST ast) {
        List<AST> packageImportsClasses = getNodesOnTheSameLevel(ast)
        String packageName = packageImportsClasses
                .findAll { it.type == GroovyTokenTypes.PACKAGE_DEF }
                .collect { it.firstChild }
                .collect { getPackageFrom(it) }
                .find() ?: ''

        packageImportsClasses
                .findAll { isClassNode(it) }
                .collect { getNodesOnTheSameLevel(it.firstChild) }
                .collect { classChildNodesToSpec(it, packageName) }
                .findAll { it.isValid() }
    }

    private static String getPackageFrom(AST packageLineContent) {
        AST dot = getNodesOnTheSameLevel(packageLineContent)
                .find { it.type == GroovyTokenTypes.DOT }
        getPackageFromDot(dot)
    }

    private static String getPackageFromDot(AST dot) {
        if (dot) {
            if (dot.type == GroovyTokenTypes.IDENT) {
                dot.text + (dot.nextSibling ? ".${dot.nextSibling.text}" : '')
            } else {
                getPackageFromDot(dot.firstChild) + (dot.nextSibling ? ".${dot.nextSibling.text}" : '')
            }
        } else {
            ''
        }
    }

    private static Spec classChildNodesToSpec(List<AST> classChildNodes, String packageName) {
        String className = classChildNodes
                .find { isIdentifierNode(it) }
                .text
        String fullClass = [packageName, className].join('.')
        Class<?> clazz = Class.forName(fullClass)
        new Spec(
                name: fullClass,
                title: getTitleFromClass(clazz),
                description: getNarrativeFromClass(clazz),
                subjects: getSubjectsFromClassAndFields(clazz) ?: null,
                scenarios: classChildNodes
                        .find { isObjectBlockNode(it) }
                        .collect { getScenarios(it) }
                        .find() ?: []
        )
    }

    private static Set<Class> getSubjectsFromClassAndFields(Class<?> clazz) {
        List<Class> classSubjects = getSubjectsFromClass(clazz)
        List<Class> fieldSubjects = getSubjectsFromFields(clazz.declaredFields as List<Field>)
        [classSubjects, fieldSubjects].flatten() as Set<Class>
    }

    private static List<Class> getSubjectsFromFields(List<Field> fields) {
        fields
                .findAll { fieldHasSubjectAnnotation(it) }
                .collectMany {
            it.annotations
                    .find { it instanceof Subject }
                    .collect { it as Subject }
                    .find()
                    .value()
                    .findAll { it != Void } ?: [it.type]
        }
    }

    private static boolean fieldHasSubjectAnnotation(Field f) {
        f.annotations.find { it instanceof Subject } != null
    }

    private static List<Class> getSubjectsFromClass(Class<?> clazz) {
        clazz.annotations
                .findAll { it instanceof Subject }
                .collect { it as Subject }
                .collect { it.value() }
                .flatten()
                .findAll { it != Void }
                .collect { it as Class }
    }

    private static String getTitleFromClass(Class<?> clazz) {
        clazz.annotations
                .findAll { it instanceof Title }
                .collect { it as Title }
                .collect { it.value() }
                .find()
    }

    private static String getNarrativeFromClass(Class<?> clazz) {
        clazz.annotations
                .findAll { it instanceof Narrative }
                .collect { it as Narrative }
                .collect { it.value() }
                .find()
    }

    private static boolean isIdentifierNode(AST ast) {
        ast.type == GroovyTokenTypes.IDENT
    }

    private static boolean isObjectBlockNode(AST ast) {
        ast.type == GroovyTokenTypes.OBJBLOCK
    }

    private static boolean isClassNode(AST ast) {
        ast.type == GroovyTokenTypes.CLASS_DEF
    }

    private static AST createAST(String code) {
        new GroovyRecognizer(
                new GroovyLexer(
                        new StringReader(code)).plumb()).with {
            p ->
                p.compilationUnit()
                p.AST
        }
    }

    private static List<Scenario> getScenarios(AST classContentNode) {
        if (classContentNode) {
            List<AST> fieldsAndMethods = getNodesOnTheSameLevel(classContentNode.firstChild)
            fieldsAndMethods
                    .findAll { isMethodNode(it) }
                    .collect { it.firstChild }
                    .collect { getScenarioFromMethod(it) }
                    .findAll { it.isValid() } ?: null
        } else {
            []
        }
    }

    private static Scenario getScenarioFromMethod(AST method) {
        new Scenario(
                name: method.nextSibling.nextSibling,
                statements:
                        getNodesOnTheSameLevel(method)
                                .findAll { isStatementListNode(it) }
                                .collectMany { getStatementsFromMethodStatements(it.firstChild) }
        )
    }

    private static boolean isMethodNode(AST fieldOrMethod) {
        fieldOrMethod.type == GroovyTokenTypes.METHOD_DEF
    }

    private static boolean isStatementListNode(AST method) {
        method.type == GroovyTokenTypes.SLIST
    }

    private static List<Statement> getStatementsFromMethodStatements(AST statements) {
        getNodesOnTheSameLevel(statements)
                .findAll { isLabelNode(it) }
                .collect { createStatementFromLabel(it) }
    }

    private static boolean isLabelNode(AST statements) {
        statements.type == GroovyTokenTypes.LABELED_STAT
    }

    private static Statement createStatementFromLabel(AST labelNode) {
        AST label = labelNode.firstChild
        new Statement(
                block: Block.valueOf(label.text.toUpperCase()),
                description: createLabelDescription(label)
        )
    }

    private static String createLabelDescription(AST label) {
        AST labelTextNode = label.nextSibling.firstChild
        hasLabelText(labelTextNode) ? labelTextNode : null
    }

    private static boolean hasLabelText(AST label) {
        label.type == GroovyTokenTypes.STRING_LITERAL
    }

    private static List<AST> getNodesOnTheSameLevel(AST first) {
        if (first) {
            [first] + getNodesOnTheSameLevel(first.nextSibling)
        } else {
            []
        }
    }

}
