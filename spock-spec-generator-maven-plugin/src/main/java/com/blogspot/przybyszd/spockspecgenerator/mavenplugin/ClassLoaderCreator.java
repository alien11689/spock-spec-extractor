package com.blogspot.przybyszd.spockspecgenerator.mavenplugin;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Set;

class ClassLoaderCreator {
    private final MavenProject mavenProject;

    public ClassLoaderCreator(MavenProject mavenProject) {
        this.mavenProject = mavenProject;
    }

    public ClassLoader prepareClassLoader() throws DependencyResolutionRequiredException, MalformedURLException {
        Set<String> elements = new HashSet<>();
        elements.addAll(mavenProject.getTestClasspathElements());
        elements.addAll(mavenProject.getRuntimeClasspathElements());
        elements.addAll(mavenProject.getSystemClasspathElements());
        elements.addAll(mavenProject.getCompileClasspathElements());

        Set<URL> urls = new HashSet<>();
        for (String element : elements) {
            urls.add(new File(element).toURI().toURL());
        }

        final URLClassLoader urlClassLoader = URLClassLoader.newInstance(
                urls.toArray(new URL[urls.size()]),
                Thread.currentThread().getContextClassLoader());

        Thread.currentThread().setContextClassLoader(urlClassLoader);

        return urlClassLoader;
    }
}
