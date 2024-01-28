package com.jdjm;

import com.jdjm.generator.DynamicGenerator;
import com.jdjm.model.MainTemplateConfig;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws TemplateException, IOException {
        String projectPath = System.getProperty("user.dir");
        String inputPath = projectPath + File.separator + "src/main/resources/templates/MainTemplate.java.ftl";
        String outputPath = projectPath + File.separator + "MainTemplate.java";
        MainTemplateConfig mainTemplateConfig = new MainTemplateConfig();
        mainTemplateConfig.setAuthor("yupiaa");
        mainTemplateConfig.setLoop(true);
        mainTemplateConfig.setOutputText("求和结果：");
        DynamicGenerator.doGenerate(inputPath, outputPath, mainTemplateConfig);
    }
}