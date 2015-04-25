package com.blogspot.przybyszd.spockspecgenerator.mavenplugin;

import com.blogspot.przybyszd.spockspecgenerator.core.SpockSpecGenerator;
import com.blogspot.przybyszd.spockspecgenerator.core.domain.Spec;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.FileReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

@Mojo(
        name="generate",
        defaultPhase = LifecyclePhase.PROCESS_TEST_CLASSES,
        threadSafe = true,
        requiresDependencyResolution = ResolutionScope.TEST,
        requiresProject = true,
        requiresDependencyCollection = ResolutionScope.TEST
)
public class GeneratorMojo extends AbstractMojo {
    @Parameter(required = true, readonly = true, defaultValue = "${project}")
    private MavenProject mavenProject;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        List<String> testSources = mavenProject.getTestCompileSourceRoots();
        try {
            Set<URL> urls = new HashSet<>();
            List<String> elements = mavenProject.getTestClasspathElements();
            for(String elem : elements){
                System.out.println("Elements: " + elem);
            }
            //getRuntimeClasspathElements()
            //getCompileClasspathElements()
            //getSystemClasspathElements()
            for (String element : elements) {
                urls.add(new File(element).toURI().toURL());
            }

            ClassLoader contextClassLoader = URLClassLoader.newInstance(
                    urls.toArray(new URL[urls.size()]),
                    Thread.currentThread().getContextClassLoader());

            Thread.currentThread().setContextClassLoader(contextClassLoader);
            List<File> spockFiles = new ArrayList<>();
            for(String testSource : testSources){
                File base = new File(testSource);
                Stack<File> stack = new Stack<>();
                stack.push(base);
                while(!stack.isEmpty()){
                    final File file = stack.pop();
                    if(file.isDirectory()){
                        for(File child : file.listFiles()){
                            stack.push(child);
                        }
                    }else if(file.getName().endsWith(".groovy")){
                        spockFiles.add(file);
                    }
                }
            }
            Collections.sort(spockFiles);
            List<Spec> specs =  new ArrayList<>();
            for (File file : spockFiles){
                List<Spec> specsFromFile = new SpockSpecGenerator().generateSpec(file, contextClassLoader);
                specs.addAll(specsFromFile);
                System.out.println(specsFromFile);
            }

        } catch (DependencyResolutionRequiredException e) {
            throw new RuntimeException(e);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
