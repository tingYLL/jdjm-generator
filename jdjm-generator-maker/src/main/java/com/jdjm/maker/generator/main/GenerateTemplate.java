package com.jdjm.maker.generator.main;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.StrUtil;
import com.jdjm.maker.generator.JarGenerator;
import com.jdjm.maker.generator.ScriptGenerator;
import com.jdjm.maker.generator.file.DynamicFileGenerator;
import com.jdjm.maker.generator.file.FileGenerator;
import com.jdjm.maker.meta.Meta;
import com.jdjm.maker.meta.MetaManager;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;
import java.net.URL;



public abstract class GenerateTemplate {
    public  void doGenerate() throws TemplateException, IOException, InterruptedException {
        Meta meta = MetaManager.getMetaObject();
        System.out.println(meta);

        //根路径位置
        String projectPath = System.getProperty("user.dir");
        System.out.println("user.dir:"+projectPath);
        String outputPath = projectPath + File.separator + "generated" + File.separator + meta.getName();
        if(!FileUtil.exist(outputPath)){
            FileUtil.mkdir(outputPath);
        }

        // 1.复制原始文件
        String sourceCopyDestPath = copySource(meta, outputPath);


        //2. 代码生成
        generateCode(meta, projectPath, outputPath);

        //3. 构建jar包
        String jarPath = buildJar(outputPath,meta);


        //4.封装脚本
        String shellOutputFilePath = buildScript(outputPath,jarPath);


        //5. 生成精简版的程序
        buildDist(outputPath, sourceCopyDestPath, shellOutputFilePath, jarPath);
    }

    protected  String buildScript(String outputPath,String jarPath) throws IOException {
        String shellOutputFilePath = outputPath + File.separator +"generator";
        ScriptGenerator.doGenerate(shellOutputFilePath,jarPath);
        return shellOutputFilePath;
    }

    protected  void buildDist(String outputPath, String sourceCopyDestPath, String shellOutputFilePath, String jarPath) {
        // 生成精简版的程序(不包含源码)
        String distOutputPath = outputPath + "-dist";
        // 拷贝jar包
        String targetAbsolutePath = distOutputPath + File.separator + "target";
        FileUtil.mkdir(targetAbsolutePath);
        String jarAbsolutePath = outputPath + File.separator + jarPath;
        FileUtil.copy(jarAbsolutePath,targetAbsolutePath,true);
        //拷贝脚本文件
        FileUtil.copy(shellOutputFilePath,distOutputPath,true);
        FileUtil.copy(shellOutputFilePath + ".bat",distOutputPath,true);

        //拷贝源模版文件
        FileUtil.copy(sourceCopyDestPath,distOutputPath,true);
    }

    protected  String buildJar(String outputPath,Meta meta) throws IOException, InterruptedException {
        //构建jar包
        JarGenerator.doGenerator(outputPath);
        String jarName = String.format("%s-%s-jar-with-dependencies.jar",meta.getName(),meta.getVersion());
        String jarPath = "target/"+jarName;
        return jarPath;
    }

    protected  void generateCode(Meta meta, String projectPath, String outputPath) throws IOException, TemplateException {
        // 读取resources 目录
//        ClassPathResource classPathResource = new ClassPathResource("");
//        String inputResourcePath = classPathResource.getAbsolutePath();
//        src/main/resources/templates
        String inputResourcePath = projectPath + File.separator + "src/main/resources";

        //从元信息中读取 Java包的基础路径
        //com.jdjm
        String outputBasePackage = meta.getBasePackage();
        //转换成com/jdjm
        String outputBasePackagePath = StrUtil.join("/",StrUtil.split(outputBasePackage,"."));
        //generated/src/main/java/com/jdjm
        String outputBaseJavaPackagePath = outputPath + File.separator +"src/main/java/" + outputBasePackagePath;

        //生成数据模型DataModel文件
        String inputFilePath = inputResourcePath + File.separator + "templates/java/model/DataModel.java.ftl";
        String outputFilePath = outputBaseJavaPackagePath + "/model/DataModel.java";
        DynamicFileGenerator.doGenerate(inputFilePath,outputFilePath, meta);

        //生成Command命令文件
        //ConfigCommand
        inputFilePath = inputResourcePath + File.separator +  "templates/java/cli/command/ConfigCommand.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/cli/command/ConfigCommand.java";
        DynamicFileGenerator.doGenerate(inputFilePath,outputFilePath, meta);
        //GenerateCommand
        inputFilePath = inputResourcePath + File.separator  + "templates/java/cli/command/GenerateCommand.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/cli/command/GenerateCommand.java";
        DynamicFileGenerator.doGenerate(inputFilePath,outputFilePath, meta);
        //ListCommand
        inputFilePath = inputResourcePath + File.separator  + "templates/java/cli/command/ListCommand.java.ftl";
        outputFilePath =  outputBaseJavaPackagePath + "/cli/command/ListCommand.java";
        DynamicFileGenerator.doGenerate(inputFilePath,outputFilePath, meta);
        //CommandExecutor
        inputFilePath = inputResourcePath + File.separator + "templates/java/cli/CommandExecutor.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/cli/CommandExecutor.java";
        DynamicFileGenerator.doGenerate(inputFilePath,outputFilePath, meta);

        //生成generate文件
        //MainGenerator
        inputFilePath = inputResourcePath + File.separator + "templates/java/generator/MainGenerator.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/generator/MainGenerator.java";
        DynamicFileGenerator.doGenerate(inputFilePath,outputFilePath, meta);
        //DynamicFileGenerator
        inputFilePath = inputResourcePath + File.separator + "templates/java/generator/DynamicFileGenerator.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/generator/DynamicFileGenerator.java";
        DynamicFileGenerator.doGenerate(inputFilePath,outputFilePath, meta);
        //StaticFileGenerator
        inputFilePath = inputResourcePath + File.separator + "templates/java/generator/StaticFileGenerator.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/generator/StaticFileGenerator.java";
        DynamicFileGenerator.doGenerate(inputFilePath,outputFilePath, meta);

        //生成pom.xml
        inputFilePath = inputResourcePath +File.separator + "templates/pom.xml.ftl";
        outputFilePath = outputPath + File.separator + "pom.xml";
        DynamicFileGenerator.doGenerate(inputFilePath,outputFilePath, meta);

        //生成README.md项目介绍文件
        inputFilePath = inputResourcePath + File.separator + "templates/README.md.ftl";
        outputFilePath = outputPath + File.separator + "README.md";
        DynamicFileGenerator.doGenerate(inputFilePath,outputFilePath, meta);
    }

    protected  String copySource(Meta meta, String outputPath) {
        //从原始模版文件路径复制到生成的代码包中
        String sourceRootPath = meta.getFileConfig().getSourceRootPath();
        String sourceCopyDestPath = outputPath + File.separator + ".source";
        FileUtil.copy(sourceRootPath,sourceCopyDestPath,false);
        return sourceCopyDestPath;
    }
}

