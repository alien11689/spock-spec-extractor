package com.blogspot.przybyszd.spockspecgenerator.mavenplugin;

import com.blogspot.przybyszd.spockspecgenerator.core.domain.Spec;
import freemarker.template.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

class SpecificationFileGenerator {
    private final String projectName;
    private final String outputDirectory;

    public SpecificationFileGenerator(String projectName, String outputDirectory) {
        this.projectName = projectName;
        this.outputDirectory = outputDirectory;
    }

    void generateSpecificationReport(List<Spec> specs) throws IOException, TemplateException {
        Template template = getSpecificationTemplate();

        Map<String, Object> input = new HashMap<>();
        input.put("projectName", projectName);
        input.put("specs", specs);

        final String specDirAsString = outputDirectory + "/../specification/";
        File specDir = new File(specDirAsString);
        specDir.mkdirs();
        try (Writer fileWriter = new FileWriter(new File(specDirAsString + "/output.html"))) {
            template.process(input, fileWriter);
        }
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
