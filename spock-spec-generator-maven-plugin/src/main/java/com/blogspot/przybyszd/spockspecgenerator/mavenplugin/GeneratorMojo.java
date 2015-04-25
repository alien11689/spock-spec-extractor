package com.blogspot.przybyszd.spockspecgenerator.mavenplugin;

import com.blogspot.przybyszd.spockspecgenerator.core.SpockSpecGenerator;
import com.blogspot.przybyszd.spockspecgenerator.core.domain.Spec;
import freemarker.template.*;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

@Mojo(
        name = "generate",
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
        try {
            ClassLoader contextClassLoader = prepareClassLoader();

            List<File> spockFiles = getSpecificationGroovyFiles();

            List<Spec> specs = generateSpecificationModel(contextClassLoader, spockFiles);

            generateSpecificationReport(specs);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void generateSpecificationReport(List<Spec> specs) throws IOException, TemplateException {
        Template template = getSpecificationTemplate();

        Map<String, Object> input = new HashMap<>();
        input.put("projectName", mavenProject.getName());
        input.put("specs", specs);

        final String specDirAsString = mavenProject.getBuild().getOutputDirectory() + "/../specification/";
        File specDir = new File(specDirAsString);
        specDir.mkdirs();
        try (Writer fileWriter = new FileWriter(new File(specDirAsString + "/output.html"))) {
            template.process(input, fileWriter);
        }
    }

    private List<Spec> generateSpecificationModel(ClassLoader contextClassLoader, List<File> spockFiles) {
        List<Spec> specs = new ArrayList<>();
        for (File file : spockFiles) {
            try {
                List<Spec> specsFromFile = new SpockSpecGenerator().generateSpec(file, contextClassLoader);
                specs.addAll(specsFromFile);
            } catch (Throwable e) {
                System.err.println(e);
                e.printStackTrace();
            }
        }
        return specs;
    }

    private Template getSpecificationTemplate() throws IOException {
        Configuration cfg = new Configuration();

        // Where do we load the templates from:
        cfg.setClassForTemplateLoading(GeneratorMojo.class, "/templates");

        // Some other recommended settings:
        cfg.setIncompatibleImprovements(new Version(2, 3, 20));
        cfg.setDefaultEncoding("UTF-8");
        cfg.setLocale(Locale.US);
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        return cfg.getTemplate("spec.ftl");
    }

    private List<File> getSpecificationGroovyFiles() {
        List<String> testSources = mavenProject.getTestCompileSourceRoots();
        List<File> spockFiles = new ArrayList<>();
        for (String testSource : testSources) {
            File base = new File(testSource);
            Stack<File> stack = new Stack<>();
            stack.push(base);
            while (!stack.isEmpty()) {
                final File file = stack.pop();
                if (file.isDirectory()) {
                    for (File child : file.listFiles()) {
                        stack.push(child);
                    }
                } else if (file.getAbsolutePath().endsWith(".groovy")) {
                    spockFiles.add(file);
                }
            }
        }
        Collections.sort(spockFiles);
        return spockFiles;
    }

    private ClassLoader prepareClassLoader() throws DependencyResolutionRequiredException, MalformedURLException {
        Set<URL> urls = new HashSet<>();
        Set<String> elements = new HashSet<>();
        elements.addAll(mavenProject.getTestClasspathElements());
        elements.addAll(mavenProject.getRuntimeClasspathElements());
        elements.addAll(mavenProject.getSystemClasspathElements());
        elements.addAll(mavenProject.getCompileClasspathElements());
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
