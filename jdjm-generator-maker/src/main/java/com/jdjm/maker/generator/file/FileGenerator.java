package com.jdjm.maker.generator.file;


import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

/**
 * 完整的生成
 */
public class FileGenerator {
    /**
     * 生成
     *
     * @param model 数据模型
     * @throws TemplateException
     * @throws IOException
     */
    public static void doGenerate(Object model) throws  TemplateException, IOException {
        String projectPath = System.getProperty("user.dir");
        System.out.println("user.dir是:"+projectPath);
        // 整个项目的根路径
        File parentFile = new File(projectPath).getParentFile();
        System.out.println("ParentFile是:"+parentFile);
        // 输入路径
        String inputPath = new File(parentFile, "jdjm-generator-demo-projects/acm-template").getAbsolutePath();
        String outputPath = projectPath;
        // 生成静态文件
        StaticFileGenerator.copyFilesByHutool(inputPath, outputPath);
        // 生成动态文件
        String inputDynamicFilePath = projectPath + File.separator + "src/main/resources/templates/MainTemplate.java.ftl";
        String outputDynamicFilePath = outputPath + File.separator + "acm-template/src/com/jdjm/acm/MainTemplate.java";
        DynamicFileGenerator.doGenerate(inputDynamicFilePath, outputDynamicFilePath, model);
    }

//    public static void main(String[] args) throws TemplateException, IOException {
//        DataModel dataModel = new DataModel();
//        dataModel.setAuthor("jdjmmml");
//        dataModel.setLoop(true);
//        dataModel.setOutputText("求和结果2：");
//        doGenerate(dataModel);
//    }
}
