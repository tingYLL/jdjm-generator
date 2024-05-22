package ${basePackage}.generator;

import ${basePackage}.model.DataModel;
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
    public static void doGenerate(DataModel model) throws  TemplateException, IOException {
        String inputRootPath = "${fileConfig.inputRootPath}";
        String outputRootPath = "${fileConfig.outputRootPath}";

        String inputPath;
        String outputPath;

        <#list fileConfig.files as fileInfo>
            inputPath = new File(inputRootPath,"${fileInfo.inputPath}").getAbsolutePath();
            outputPath = new File(outputRootPath,"${fileInfo.outputPath}").getAbsolutePath();
            <#if fileInfo.generateType == "static">
                StaticFileGenerator.copyFilesByHutool(inputPath, outputPath);
            <#else>
                DynamicFileGenerator.doGenerate(inputPath, outputPath, model);
            </#if>
        </#list>
    }
}
