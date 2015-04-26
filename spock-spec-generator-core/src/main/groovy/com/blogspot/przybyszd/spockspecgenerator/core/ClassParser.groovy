package com.blogspot.przybyszd.spockspecgenerator.core

import com.blogspot.przybyszd.spockspecgenerator.core.domain.Ignored
import spock.lang.*

import java.lang.reflect.Field

class ClassParser {
    static Set<Class> getSubjectsFromClassAndFields(Class<?> clazz) {
        List<Class> classSubjects = getSubjectsFromClass(clazz)
        List<Class> fieldSubjects = getSubjectsFromFields(clazz.declaredFields as List<Field>)
        [classSubjects, fieldSubjects].flatten() as Set<Class>
    }

    private static List<Class> getSubjectsFromFields(List<Field> fields) {
        fields
                .findAll { fieldHasSubjectAnnotation(it) }
                .collectMany { getSubjectClassesFromField(it) }
    }

    private static List<Class> getSubjectClassesFromField(Field field) {
        field.annotations
                .find { it instanceof Subject }
                .collect { it as Subject }
                .find()
                .value()
                .findAll { it != Void } ?: [field.type]
    }

    private static boolean fieldHasSubjectAnnotation(Field f) {
        f.annotations.find { it instanceof Subject } != null
    }

    static Set<String> getIssuesFromClass(Class<?> clazz) {
        clazz.annotations
                .findAll { it instanceof Issue }
                .collect { it as Issue }
                .collect { it.value() }
                .flatten()
                .collect { it as String }
    }

    static Set<String> getSeesFromClass(Class<?> clazz) {
        clazz.annotations
                .findAll { it instanceof See }
                .collect { it as See }
                .collect { it.value() }
                .flatten()
                .collect { it as String }
    }

    static Ignored getIgnoredFromClass(Class<?> clazz) {
        clazz.annotations
                .findAll { it instanceof Ignore }
                .collect { it as Ignore }
                .collect { new Ignored(it.value()) }
                .find()
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

    static String getTitleFromClass(Class<?> clazz) {
        clazz.annotations
                .findAll { it instanceof Title }
                .collect { it as Title }
                .collect { it.value() }
                .find()
    }

    static String getNarrativeFromClass(Class<?> clazz) {
        clazz.annotations
                .findAll { it instanceof Narrative }
                .collect { it as Narrative }
                .collect { it.value() }
                .find()
    }
}
