package com.blogspot.przybyszd.spockspecgenerator.mavenplugin;

import com.blogspot.przybyszd.spockspecgenerator.core.SpockSpecGenerator;
import com.blogspot.przybyszd.spockspecgenerator.core.domain.Block;
import com.blogspot.przybyszd.spockspecgenerator.core.domain.Scenario;
import com.blogspot.przybyszd.spockspecgenerator.core.domain.Spec;
import com.blogspot.przybyszd.spockspecgenerator.core.domain.Statement;
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
import java.util.regex.Pattern;

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

    @Parameter(required = false, readonly = true)
    private String pattern = ".*\\.groovy$";

    @Parameter(required = false, readonly = true)
    private List<Block> omitBlocks = Collections.singletonList(Block.WHERE);

    @Parameter(required = false, readonly = true)
    private boolean mergeAndBlock = false;

    @Parameter(required = false, readonly = true)
    private boolean omitBlocksWithoutDescription = false;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            debugPrintParameters();

            ClassLoader contextClassLoader = prepareClassLoader();

            List<File> spockFiles = getSpecificationGroovyFiles();

            getLog().debug("Files to generate specification: " + spockFiles);

            List<Spec> specs = generateSpecificationModel(contextClassLoader, spockFiles);

            getLog().debug("Generated specification model: " + specs);

            generateSpecificationReport(specs);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void debugPrintParameters() {
        getLog().debug("Pattern: " + pattern);
        getLog().debug("Omit blocks: " + omitBlocks);
        getLog().debug("Merge And block: " + mergeAndBlock);
        getLog().debug("Omit block without description: " + omitBlocksWithoutDescription);
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
            specs.addAll(applyParameterToSpecs(SpockSpecGenerator.generateSpec(file, contextClassLoader)));
            getLog().info("Generated specification model for file " + file.getAbsolutePath());
        }
        return specs;
    }

    private List<Spec> applyParameterToSpecs(List<Spec> specs) {
        List<Spec> newSpecs = new ArrayList<>();
        for (Spec spec : specs) {
            Spec newSpec = new Spec(
                    spec.getName(),
                    spec.getTitle(),
                    spec.getDescription(),
                    spec.getSubjects(),
                    applyParameterToScenarios(spec.getScenarios()),
                    spec.getIssues(),
                    spec.getLinks());
            newSpecs.add(newSpec);
        }
        return newSpecs;
    }

    private List<Scenario> applyParameterToScenarios(List<Scenario> scenarios) {
        List<Scenario> newScenarios = new ArrayList<>();
        for (Scenario scenario : scenarios) {
            Scenario newScenario = new Scenario(
                    scenario.getName(),
                    applyParameterToStatements(scenario.getStatements()),
                    scenario.getIssues(),
                    scenario.getLinks()
            );
            newScenarios.add(newScenario);
        }
        return newScenarios;
    }

    private List<Statement> applyParameterToStatements(List<Statement> statements) {
        List<Statement> newStatements = new ArrayList<>();
        for (final Statement currentStatement : statements) {
            if (omitBlocksWithoutDescription && currentStatement.getDescription() == null) {
                continue;
            }
            if (omitBlocks.contains(currentStatement.getBlock())) {
                continue;
            }
            if (shouldMergeAndBlock(newStatements, currentStatement)) {
                final int lastStatementIndex = newStatements.size() - 1;
                Statement lastStatement = newStatements.get(lastStatementIndex);
                String newDescription = mergeDescriptionsWithAnd(currentStatement, lastStatement);
                Statement newStatement = new Statement(lastStatement.getBlock(), newDescription);
                newStatements.set(lastStatementIndex, newStatement);
            } else {
                newStatements.add(currentStatement);
            }
        }
        return newStatements;
    }

    private String mergeDescriptionsWithAnd(Statement currentStatement, Statement lastStatement) {
        List<String> description = new ArrayList<>();
        if (lastStatement.getDescription() != null) {
            description.add(lastStatement.getDescription());
        }
        if (currentStatement.getDescription() != null) {
            description.add(currentStatement.getDescription());
        }
        if (description.size() == 2) {
            return description.get(0) + " and " + description.get(1);
        } else if (description.size() == 1) {
            return description.get(1);
        }
        return null;
    }

    private boolean shouldMergeAndBlock(List<Statement> newStatements, Statement currentStatement) {
        return mergeAndBlock
                && currentStatement.getBlock() == Block.AND
                && !newStatements.isEmpty();
    }

    private List<File> getSpecificationGroovyFiles() {
        List<String> testSources = mavenProject.getTestCompileSourceRoots();
        List<File> spockFiles = new ArrayList<>();
        Pattern filePattern = Pattern.compile(pattern);
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
                } else if (filePattern.matcher(file.getAbsolutePath()).matches()) {
                    spockFiles.add(file);
                }
            }
        }
        Collections.sort(spockFiles);
        return spockFiles;
    }

    private ClassLoader prepareClassLoader() throws DependencyResolutionRequiredException, MalformedURLException {
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

    private Template getSpecificationTemplate() throws IOException {
        Configuration cfg = new Configuration();
        cfg.setClassForTemplateLoading(GeneratorMojo.class, "/templates");
        cfg.setIncompatibleImprovements(new Version(2, 3, 20));
        cfg.setDefaultEncoding("UTF-8");
        cfg.setLocale(Locale.US);
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        return cfg.getTemplate("spec.ftl");
    }
}
