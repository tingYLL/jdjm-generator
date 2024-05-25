package ${basePackage}.generator;

import ${basePackage}.model.DataModel;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

/**
 * 完整的生成
 */

<#macro generateFile indent fileInfo>
${indent}inputPath = new File(inputRootPath,"${fileInfo.inputPath}").getAbsolutePath();
${indent}outputPath = new File(outputRootPath,"${fileInfo.outputPath}").getAbsolutePath();
<#if fileInfo.generateType == "static">
${indent}StaticFileGenerator.copyFilesByHutool(inputPath, outputPath);
<#else>
${indent}DynamicFileGenerator.doGenerate(inputPath, outputPath, model);
</#if>
</#macro>

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

<#list modelConfig.models as modelInfo>
        ${modelInfo.type} ${modelInfo.fieldName} = model.${modelInfo.fieldName};
</#list>

<#list fileConfig.files as fileInfo>
    <#if fileInfo.groupKey??>
        //groupKey = ${fileInfo.groupKey}
        <#if fileInfo.condition??>
        if(${fileInfo.condition}){
            <#list fileInfo.files as fileInfo>
               <@generateFile indent="               " fileInfo=fileInfo />
            </#list>
        }
        <#else>
            <#list fileInfo.files as fileInfo>
                <@generateFile indent="               " fileInfo=fileInfo />
            </#list>
        </#if>
    <#else>
        <@generateFile indent="        " fileInfo=fileInfo />
    </#if>
</#list>
    }
}
