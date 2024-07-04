package com.jdjm.maker.template;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.jdjm.maker.generator.file.FileGenerator;
import com.jdjm.maker.meta.Meta;
import com.jdjm.maker.meta.enums.FileGenerateTypeEnum;
import com.jdjm.maker.meta.enums.FileTypeEnum;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class TemplateMaker {

    public static void main(String[] args) {
        //这里会在当前的maker目录下生成一个临时的目录
        //指定原始项目路径
        String projectPath = System.getProperty("user.dir");
        String originProjectPath = FileUtil.getAbsolutePath(new File(projectPath).getParentFile()) + File.separator + "jdjm-generator-demo-projects/acm-template-pro";

        //复制目录
        long id = IdUtil.getSnowflakeNextId();
        String tempDirPath = projectPath + File.separator + ".temp";
        String templatePath = tempDirPath + File.separator + id;

        if(!FileUtil.exist(templatePath)){
            FileUtil.mkdir(templatePath);
        }

        FileUtil.copy(originProjectPath,templatePath,true);

        //一、输入信息
        //1.项目的基本信息
        String name = "acm-template-generator";
        String description = "ACM 示例模板生成器";

        //2.输入文件信息
        //拼接出完整的要挖坑的项目根目录，这里直接拿当前maker项目里的。注意要记得拼上originProjectPath路径下最后一层的文件夹的名称
        String sourceRootPath = templatePath + File.separator + FileUtil.getLastPathEle(Paths.get(originProjectPath)).toString();
        sourceRootPath = sourceRootPath.replace("\\","/");
        //或者sourceRootPath = sourceRootPath.replaceAll("\\\\","/");
        //要挖坑的文件
        String fileInputPath = "src/com/jdjm/acm/MainTemplate.java";
        String fileOutpuhPath = fileInputPath + ".ftl";

        //3.输入模型参数
        Meta.ModelConfig.ModelInfo modelInfo = new Meta.ModelConfig.ModelInfo();
        modelInfo.setFieldName("outputText");
        modelInfo.setType("String");
        modelInfo.setDefaultValue("sum = ");

        //二、 使用字符串替换，生成模板文件
        String fileInputAbsolutePath = sourceRootPath + File.separator + fileInputPath;
        String fileContent = FileUtil.readUtf8String(fileInputAbsolutePath);
        String replacement = String.format("${%s}",modelInfo.getFieldName());
        String newFileContent = StrUtil.replace(fileContent,"Sum: ",replacement);

        //输出模板文件
        String fileOutputAbsolutePath =  sourceRootPath + File.separator +fileOutpuhPath;
        FileUtil.writeUtf8String(newFileContent,fileOutputAbsolutePath);

        //三、生成配置文件
        String metaOutputPath = sourceRootPath + File.separator + "meta.json";

        //1.构造文件信息 （fileConfig）
        Meta meta = new Meta();
        meta.setName(name);
        meta.setDescription(description);
        Meta.FileConfig fileConfig = new Meta.FileConfig();
        meta.setFileConfig(fileConfig);
        fileConfig.setSourceRootPath(sourceRootPath);
        List<Meta.FileConfig.FileInfo> fileInfoList = new ArrayList<>();
        fileConfig.setFiles(fileInfoList);
        Meta.FileConfig.FileInfo fileInfo = new Meta.FileConfig.FileInfo();
        fileInfo.setInputPath(fileInputPath);
        fileInfo.setOutputPath(fileOutpuhPath);
        fileInfo.setType(FileTypeEnum.FILE.getValue());
        fileInfo.setGenerateType(FileGenerateTypeEnum.DYNAMIC.getValue());
        fileInfoList.add(fileInfo);

        //2.构造模型信息 (modelConfig)
        Meta.ModelConfig modelConfig = new Meta.ModelConfig();
        meta.setModelConfig(modelConfig);
        List<Meta.ModelConfig.ModelInfo> modelInfoList = new ArrayList<>();
        modelInfoList.add(modelInfo);
        modelConfig.setModels(modelInfoList);

        // 通过对象生成格式化好的字符串，即格式化好的meta.json
        String s = JSONUtil.toJsonPrettyStr(meta);
        FileUtil.writeUtf8String(s,metaOutputPath);
    }
}
