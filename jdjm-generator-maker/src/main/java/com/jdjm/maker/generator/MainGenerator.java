package com.jdjm.maker.generator;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.StrUtil;
import com.jdjm.maker.generator.file.DynamicFileGenerator;
import com.jdjm.maker.generator.file.FileGenerator;
import com.jdjm.maker.meta.Meta;
import com.jdjm.maker.meta.MetaManager;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class MainGenerator {
    public static void main(String[] args) throws TemplateException, IOException {
        Meta meta = MetaManager.getMetaObject();
        System.out.println(meta);

        //根路径位置
        String projectPath = System.getProperty("user.dir");
        System.out.println("user.dir:"+projectPath);
        String outputPath = projectPath + File.separator + "generated";
        if(!FileUtil.exist(outputPath)){
            FileUtil.mkdir(outputPath);
        }

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

        String inputFilePath = inputResourcePath + File.separator + "templates/java/model/DataModel.java.ftl";
        String outputFilePath = outputBaseJavaPackagePath + "/model/DataModel.java";
        DynamicFileGenerator.doGenerate(inputFilePath,outputFilePath,meta);
    }
}
