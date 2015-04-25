package com.blogspot.przybyszd.spockspecgenerator.core

import groovy.transform.PackageScope
import groovyjarjarantlr.collections.AST
import org.codehaus.groovy.antlr.parser.GroovyTokenTypes

@PackageScope
class AstUtil {

    static boolean nodeIsPackageDef(AST node) {
        node.type == GroovyTokenTypes.PACKAGE_DEF
    }

    static boolean isStringLiteralNode(AST node) {
        node.type == GroovyTokenTypes.STRING_LITERAL
    }

    static boolean isLabelNode(AST node) {
        node.type == GroovyTokenTypes.LABELED_STAT
    }

    static boolean isMethodNode(AST node) {
        node.type == GroovyTokenTypes.METHOD_DEF
    }

    static boolean isStatementListNode(AST node) {
        node.type == GroovyTokenTypes.SLIST
    }

    static boolean isIdentifierNode(AST node) {
        node.type == GroovyTokenTypes.IDENT
    }

    static boolean isObjectBlockNode(AST node) {
        node.type == GroovyTokenTypes.OBJBLOCK
    }

    static boolean isClassNode(AST node) {
        node.type == GroovyTokenTypes.CLASS_DEF
    }

    static boolean isAnnotationNode(AST node) {
        node.type == GroovyTokenTypes.ANNOTATION
    }

    static boolean isDotNode(AST node) {
        node.type == GroovyTokenTypes.DOT
    }

    static List<AST> getNodesOnTheSameLevel(AST first) {
        if (first) {
            [first] + getNodesOnTheSameLevel(first.nextSibling)
        } else {
            []
        }
    }
}
