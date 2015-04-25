package com.blogspot.przybyszd.spockspecgenerator.mavenplugin;

import com.blogspot.przybyszd.spockspecgenerator.core.domain.Block;
import com.blogspot.przybyszd.spockspecgenerator.core.domain.Spec;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
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

            ClassLoader contextClassLoader = new ClassLoaderCreator(mavenProject).prepareClassLoader();

            List<File> spockFiles = getSpecificationGroovyFiles();

            getLog().debug("Files to generate specification: " + spockFiles);

            List<Spec> specs = new SpecificationModelGeneratorWrapper(getLog()).generateSpecificationModel(contextClassLoader, spockFiles);

            getLog().debug("Generated specification model: " + specs);

            PostModelGenerationProcessor postProcessor = new PostModelGenerationProcessor(omitBlocksWithoutDescription, omitBlocks, mergeAndBlock);

            List<Spec> processedSpecs = postProcessor.applyParameterToSpecs(specs);

            getLog().debug("Post processed specification model: " + processedSpecs);

            new SpecificationFileGenerator(mavenProject.getName(), mavenProject.getBuild().getOutputDirectory()).generateSpecificationReport(processedSpecs);

        } catch (Exception e) {
            getLog().error("Error during processing Spock specs", e);
            throw new RuntimeException(e);
        }
    }

    private void debugPrintParameters() {
        getLog().debug("Pattern: " + pattern);
        getLog().debug("Omit blocks: " + omitBlocks);
        getLog().debug("Merge And block: " + mergeAndBlock);
        getLog().debug("Omit block without description: " + omitBlocksWithoutDescription);
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
}
