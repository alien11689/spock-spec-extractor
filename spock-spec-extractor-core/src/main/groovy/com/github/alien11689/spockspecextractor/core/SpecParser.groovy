package com.github.alien11689.spockspecextractor.core

import com.github.alien11689.spockspecextractor.core.domain.*
import groovy.transform.PackageScope
import groovyjarjarantlr.collections.AST

import static com.github.alien11689.spockspecextractor.core.AstUtil.*
import static com.github.alien11689.spockspecextractor.core.ClassParser.*

@PackageScope
class SpecParser {
    private final ClassLoader classLoader;

    SpecParser(ClassLoader classLoader) {
        this.classLoader = classLoader
    }

    List<Spec> getSpecs(AST fileAst) {
        List<AST> packageImportsClasses = getNodesOnTheSameLevel(fileAst)
        String packageName = getPackageName(packageImportsClasses)

        packageImportsClasses
                .findAll { isClassNode(it) }
                .collect { getNodesOnTheSameLevel(it.firstChild) }
                .collect { classChildNodesToSpec(it, packageName) }
                .findAll { it.isValid() }
    }

    private static String getPackageName(List<AST> packageImportsClasses) {
        packageImportsClasses
                .findAll { nodeIsPackageDef(it) }
                .collect { it.firstChild }
                .collect { getPackageFrom(it) }
                .find() ?: ''
    }

    private static String getPackageFrom(AST packageLineContent) {
        AST dot = getNodesOnTheSameLevel(packageLineContent)
                .find { isDotNode(it) }
        getPackageFromDot(dot)
    }

    private static String getPackageFromDot(AST dot) {
        if (dot) {
            if (isIdentifierNode(dot)) {
                dot.text + (dot.nextSibling ? ".${dot.nextSibling.text}" : '')
            } else {
                getPackageFromDot(dot.firstChild) + (dot.nextSibling ? ".${dot.nextSibling.text}" : '')
            }
        } else {
            ''
        }
    }

    private Spec classChildNodesToSpec(List<AST> classChildNodes, String packageName) {
        String className = classChildNodes
                .find { isIdentifierNode(it) }
                .text
        String fullClass = [packageName, className].join('.')
        Class<?> clazz = Class.forName(fullClass, false, classLoader)
        new Spec(
                name: fullClass,
                title: getTitleFromClass(clazz),
                description: getNarrativeFromClass(clazz),
                subjects: getSubjectsFromClassAndFields(clazz) ?: null,
                links: getSeesFromClass(clazz) ?: null,
                issues: getIssuesFromClass(clazz) ?: null,
                ignored: getIgnoredFromClass(clazz),
                scenarios: classChildNodes
                        .find { isObjectBlockNode(it) }
                        .collect { getScenarios(it) }
                        .find() ?: []
        )
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
        String methodName = method.nextSibling.nextSibling
        new Scenario(
                name: methodName,
                links: getAnnotationValues(method, 'See') ?: null,
                issues: getAnnotationValues(method, 'Issue') ?: null,
                ignored: getIgnoredAnnotation(method),
                statements:
                        getNodesOnTheSameLevel(method)
                                .findAll { isStatementListNode(it) }
                                .collectMany { getStatementsFromMethodStatements(it.firstChild) }
        )
    }

    private static Ignored getIgnoredAnnotation(AST method) {
        getNodesOnTheSameLevel(method.firstChild)
                .findAll { isAnnotationNode(it) }
                .collect { it.firstChild }
                .findAll { it.text == "Ignore" }
                .collect {
            new Ignored(it.nextSibling?.firstChild?.nextSibling?.text)
        }.find()
    }

    private static Set<String> getAnnotationValues(AST method, String annotationName) {
        getNodesOnTheSameLevel(method.firstChild)
                .findAll { isAnnotationNode(it) }
                .collect { it.firstChild }
                .findAll { it.text == annotationName }
                .collect { it.nextSibling.firstChild.nextSibling }
                .collectMany {
            if (it.firstChild) {
                it.firstChild.firstChild.collect { getNodesOnTheSameLevel(it) }.collect { it.firstChild.text }
            } else {
                [it.text]
            }
        }.flatten()
                .collect { it as String }
    }

    private static List<Statement> getStatementsFromMethodStatements(AST statements) {
        getNodesOnTheSameLevel(statements)
                .findAll { isLabelNode(it) }
                .collect { createStatementFromLabel(it) }
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
        isStringLiteralNode(labelTextNode) ? labelTextNode : null
    }
}
