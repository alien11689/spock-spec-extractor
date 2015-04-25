package com.blogspot.przybyszd.spockspecgenerator.core

import groovyjarjarantlr.TokenStream
import org.codehaus.groovy.antlr.parser.GroovyRecognizer

class MyGroovyRecognizer extends GroovyRecognizer {
    MyGroovyRecognizer(TokenStream lexer) {
        super(lexer)
    }

    @Override
    void addWarning(String warning, String solution) {

    }
}
