package com.github.alien11689.spockspecextractor.core

import groovy.transform.PackageScope
import groovyjarjarantlr.collections.AST
import org.codehaus.groovy.antlr.parser.GroovyLexer

@PackageScope
class CodeParser {
    static AST createAST(String code) {
        new MyGroovyRecognizer(
                new GroovyLexer(
                        new StringReader(code)).plumb()).with {
            p ->
                p.compilationUnit()
                p.AST
        }
    }
}
