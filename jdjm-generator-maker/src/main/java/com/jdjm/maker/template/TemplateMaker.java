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
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class TemplateMaker {

    private static long makeTemplate(Long id){
        //没有id则生成
        if(id == null){
            id = IdUtil.getSnowflakeNextId();
        }
        //业务逻辑...
        String projectPath = System.getProperty("user.dir");
        //指定原始项目路径
        String originProjectPath = FileUtil.getAbsolutePath(new File(projectPath).getParentFile()) + File.separator + "jdjm-generator-demo-projects/acm-template-pro";

        String tempDirPath = projectPath + File.separator + ".temp";
        String templatePath = tempDirPath + File.separator + id;

        if(!FileUtil.exist(templatePath)){
            FileUtil.mkdir(templatePath);
            //在当前的maker目录下的.tmp文件夹下生成一个临时文件夹
            FileUtil.copy(originProjectPath,templatePath,true);
        }

        String name = "acm-template-generator";
        String description = "ACM 示例模板生成器";

        //拼接出完整目录，注意要记得拼上originProjectPath路径下最后一层的文件夹的名称
        String sourceRootPath = templatePath + File.separator + FileUtil.getLastPathEle(Paths.get(originProjectPath)).toString();
        sourceRootPath = sourceRootPath.replace("\\","/");
        //或者sourceRootPath = sourceRootPath.replaceAll("\\\\","/");
        String fileInputPath = "src/com/jdjm/acm/MainTemplate.java";
        String fileOutpuhPath = fileInputPath + ".ftl";

        //3.输入模型参数
        Meta.ModelConfig.ModelInfo modelInfo = new Meta.ModelConfig.ModelInfo();
        modelInfo.setFieldName("outputText");
        modelInfo.setType("String");
        modelInfo.setDefaultValue("sum = ");

        String fileInputAbsolutePath = sourceRootPath + File.separator + fileInputPath;
        String fileOutputAbsolutePath =  sourceRootPath + File.separator +fileOutpuhPath;
        String fileContent;
        //如果有模板文件，表示不是第一次制作，因此应该继续在原有的模板基础上继续挖坑
        if(FileUtil.exist(fileOutputAbsolutePath)){
            fileContent = FileUtil.readUtf8String(fileOutputAbsolutePath);
        }else{
            fileContent = FileUtil.readUtf8String(fileInputAbsolutePath);
        }
        String replacement = String.format("${%s}",modelInfo.getFieldName());
        String newFileContent = StrUtil.replace(fileContent,"Sum: ",replacement);
        //输出模板文件
        FileUtil.writeUtf8String(newFileContent,fileOutputAbsolutePath);


        Meta.FileConfig.FileInfo fileInfo = new Meta.FileConfig.FileInfo();
        fileInfo.setInputPath(fileInputPath);
        fileInfo.setOutputPath(fileOutpuhPath);
        fileInfo.setType(FileTypeEnum.FILE.getValue());
        fileInfo.setGenerateType(FileGenerateTypeEnum.DYNAMIC.getValue());

        //配置文件
        String metaOutputPath = sourceRootPath + File.separator + "meta.json";

        //如果已有meta.json，说明不是第一次制作，应该在原有文件上继续修改
        if(FileUtil.exist(metaOutputPath)){
            //先以字符串的形式读取meta.json，后转成对象
            Meta oldMeta = JSONUtil.toBean(FileUtil.readUtf8String(metaOutputPath),Meta.class);
            //追加参数配置
            List<Meta.FileConfig.FileInfo> fileInfoList = oldMeta.getFileConfig().getFiles();
            fileInfoList.add(fileInfo);
            List<Meta.ModelConfig.ModelInfo> modelInfoList = oldMeta.getModelConfig().getModels();
            modelInfoList.add(modelInfo);
            //输出meta.json元信息文件
            FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(oldMeta),metaOutputPath);
        }else{
            //1.构造文件信息 （fileConfig）
            Meta meta = new Meta();
            meta.setName(name);
            meta.setDescription(description);
            Meta.FileConfig fileConfig = new Meta.FileConfig();
            meta.setFileConfig(fileConfig);
            fileConfig.setSourceRootPath(sourceRootPath);
            List<Meta.FileConfig.FileInfo> fileInfoList = new ArrayList<>();
            fileConfig.setFiles(fileInfoList);
//            Meta.FileConfig.FileInfo fileInfo = new Meta.FileConfig.FileInfo();
//            fileInfo.setInputPath(fileInputPath);
//            fileInfo.setOutputPath(fileOutpuhPath);
//            fileInfo.setType(FileTypeEnum.FILE.getValue());
//            fileInfo.setGenerateType(FileGenerateTypeEnum.DYNAMIC.getValue());
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

        return id;
    }

    //对FileInfoList进行去重
    private static List<Meta.FileConfig.FileInfo> distinctFiles(List<Meta.FileConfig.FileInfo> fileInfoList){
        Collection<Meta.FileConfig.FileInfo> values = fileInfoList.stream().collect(Collectors.toMap(Meta.FileConfig.FileInfo::getInputPath, o -> o, (e, r) -> r)).values();
        List<Meta.FileConfig.FileInfo> newFileInfoList = new ArrayList<>(values);
        return newFileInfoList;
    }

    public static void main(String[] args) {
        TemplateMaker.makeTemplate(1808886735329832960L);
    }
}
