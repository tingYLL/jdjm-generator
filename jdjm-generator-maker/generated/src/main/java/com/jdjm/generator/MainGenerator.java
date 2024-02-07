package com.jdjm.generator;

import com.jdjm.model.DataModel;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

/**
 * 完整的生成
 */
public class MainGenerator {
    /**
     * 生成
     *
     * @param model 数据模型
     * @throws TemplateException
     * @throws IOException
     */
    public static void doGenerate(Object model) throws  TemplateException, IOException {
        String inputRootPath = ".source/acm-template-pro";
        String outputRootPath = "generated";

        String inputPath;
        String outputPath;

            inputPath = new File(inputRootPath,"src/com/yupi/acm/MainTemplate.java.ftl").getAbsolutePath();
            outputPath = new File(outputRootPath,"src/com/yupi/acm/MainTemplate.java").getAbsolutePath();
                DynamicFileGenerator.doGenerate(inputDynamicFilePath, outputDynamicFilePath, model);
            inputPath = new File(inputRootPath,".gitignore").getAbsolutePath();
            outputPath = new File(outputRootPath,".gitignore").getAbsolutePath();
                StaticFileGenerator.copyFilesByHutool(inputPath, outputPath);
            inputPath = new File(inputRootPath,"README.md").getAbsolutePath();
            outputPath = new File(outputRootPath,"README.md").getAbsolutePath();
                StaticFileGenerator.copyFilesByHutool(inputPath, outputPath);
    }
}
