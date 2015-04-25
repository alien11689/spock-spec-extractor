package com.blogspot.przybyszd.spockspecgenerator.core

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
