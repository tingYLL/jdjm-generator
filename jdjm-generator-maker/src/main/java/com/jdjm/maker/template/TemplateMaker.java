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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class TemplateMaker {

    /**
     * 制作模版
     * @param newMeta
     * @param originProjectPath 原始项目路径
     * @param fileInputPathList
     * @param modelInfo
     * @param searchStr
     * @param id
     * @return
     */
    private static long makeTemplate(Meta newMeta,String originProjectPath,List<String> fileInputPathList,Meta.ModelConfig.ModelInfo modelInfo,String searchStr,Long id){
        //没有id则生成
        if(id == null){
            id = IdUtil.getSnowflakeNextId();
        }
        String projectPath = System.getProperty("user.dir");
        System.out.println("user.dir:"+projectPath);
        String tempDirPath = projectPath + File.separator + ".temp";
        String templatePath = tempDirPath + File.separator + id;

        if(!FileUtil.exist(templatePath)){
            FileUtil.mkdir(templatePath);
            //在当前的maker目录下的.tmp文件夹下生成一个临时文件夹
            FileUtil.copy(originProjectPath,templatePath,true);
        }


        //拼接出完整目录，注意要记得拼上originProjectPath路径下最后一层的文件夹的名称
        String sourceRootPath = templatePath + File.separator + FileUtil.getLastPathEle(Paths.get(originProjectPath)).toString();
        sourceRootPath = sourceRootPath.replace("\\","/");
        //或者sourceRootPath = sourceRootPath.replaceAll("\\\\","/");

        List<Meta.FileConfig.FileInfo> newFileInfoList = new ArrayList<>();
        for (String fileInputPath : fileInputPathList) {
            String inputFileAbsolutePath = sourceRootPath + File.separator + fileInputPath;
            if(FileUtil.isDirectory(inputFileAbsolutePath)){
                List<File> fileList = FileUtil.loopFiles(inputFileAbsolutePath);
                for (File file : fileList) {
                    Meta.FileConfig.FileInfo fileInfo = makeFileTemplate(modelInfo, searchStr, sourceRootPath,file);
                    newFileInfoList.add(fileInfo);
                }
            }else{
                Meta.FileConfig.FileInfo fileInfo = makeFileTemplate(modelInfo, searchStr, sourceRootPath,new File(inputFileAbsolutePath));
                newFileInfoList.add(fileInfo);
            }
        }
        //配置文件
        String metaOutputPath = sourceRootPath + File.separator + "meta.json";

        //如果已有meta.json，说明不是第一次制作，应该在原有文件上继续修改
        if(FileUtil.exist(metaOutputPath)){
            //先以字符串的形式读取meta.json，后转成对象
            Meta oldMeta = JSONUtil.toBean(FileUtil.readUtf8String(metaOutputPath),Meta.class);
            //对fileConfig和modelConfig追加参数配置
            List<Meta.FileConfig.FileInfo> fileInfoList = oldMeta.getFileConfig().getFiles();
            fileInfoList.addAll(newFileInfoList);
            List<Meta.ModelConfig.ModelInfo> modelInfoList = oldMeta.getModelConfig().getModels();
            modelInfoList.add(modelInfo);
            //去重
            oldMeta.getFileConfig().setFiles(distinctFiles(fileInfoList));
            oldMeta.getModelConfig().setModels(distinctModels(modelInfoList));
            //输出meta.json元信息文件
            FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(oldMeta),metaOutputPath);
        }else{
            //1.构造文件信息 （fileConfig）
            Meta.FileConfig fileConfig = new Meta.FileConfig();
            newMeta.setFileConfig(fileConfig);
            fileConfig.setSourceRootPath(sourceRootPath);
            List<Meta.FileConfig.FileInfo> fileInfoList = new ArrayList<>();
            fileConfig.setFiles(fileInfoList);
            fileInfoList.addAll(newFileInfoList);
            //2.构造模型信息 (modelConfig)
            Meta.ModelConfig modelConfig = new Meta.ModelConfig();
            newMeta.setModelConfig(modelConfig);
            List<Meta.ModelConfig.ModelInfo> modelInfoList = new ArrayList<>();
            modelInfoList.add(modelInfo);
            modelConfig.setModels(modelInfoList);
            // 通过对象生成格式化好的字符串，即格式化好的meta.json
            String s = JSONUtil.toJsonPrettyStr(newMeta);
            FileUtil.writeUtf8String(s,metaOutputPath);
        }

        return id;
    }

    /**
     * 制作模板文件
     * @param modelInfo
     * @param searchStr
     * @param sourceRootPath
     * @param inputFile
     * @return
     */
    private static Meta.FileConfig.FileInfo makeFileTemplate(Meta.ModelConfig.ModelInfo modelInfo, String searchStr, String sourceRootPath,File inputFile) {
        String fileInputAbsolutePath = inputFile.getAbsolutePath();
        fileInputAbsolutePath = fileInputAbsolutePath.replace("\\","/");
        //截去前半部分路径
        String fileInputPath = fileInputAbsolutePath.replace(sourceRootPath+"/","");
        String fileOutpuhPath = fileInputPath + ".ftl";

        String fileOutputAbsolutePath =  fileInputAbsolutePath+".ftl";
        String fileContent;
        //如果有.ftl模板文件，表示不是第一次制作，因此应该读入已有的这个ftl文件继续挖坑
        if(FileUtil.exist(fileOutputAbsolutePath)){
            fileContent = FileUtil.readUtf8String(fileOutputAbsolutePath);
        }else{
            //否则是首次制作，应该直接读入原始的java文件
            fileContent = FileUtil.readUtf8String(fileInputAbsolutePath);
        }
        //开始挖坑
        String replacement = String.format("${%s}", modelInfo.getFieldName());
        String newFileContent = StrUtil.replace(fileContent, searchStr,replacement);


        Meta.FileConfig.FileInfo fileInfo = new Meta.FileConfig.FileInfo();
        fileInfo.setInputPath(fileInputPath);
        fileInfo.setOutputPath(fileOutpuhPath);
        fileInfo.setType(FileTypeEnum.FILE.getValue());

        //判断内容是否一致，如果和原文件内容一致，则输出为静态文件
        if(newFileContent.equals(fileContent)){
            fileInfo.setOutputPath(fileInputPath);
            fileInfo.setGenerateType(FileGenerateTypeEnum.STATIC.getValue());
        }else{
            fileInfo.setGenerateType(FileGenerateTypeEnum.DYNAMIC.getValue());
            //如果为动态生成，才需要输出 .ftl模板文件
            FileUtil.writeUtf8String(newFileContent,fileOutputAbsolutePath);
        }
        return fileInfo;
    }


    //对FileInfoList进行去重
    private static List<Meta.FileConfig.FileInfo> distinctFiles(List<Meta.FileConfig.FileInfo> fileInfoList){
        Collection<Meta.FileConfig.FileInfo> values = fileInfoList.stream().collect(Collectors.toMap(Meta.FileConfig.FileInfo::getInputPath, o -> o, (e, r) -> r)).values();
        List<Meta.FileConfig.FileInfo> newFileInfoList = new ArrayList<>(values);
        return newFileInfoList;
    }


    //对ModelInfoList进行去重
    private static List<Meta.ModelConfig.ModelInfo> distinctModels(List<Meta.ModelConfig.ModelInfo> modelInfoList){
        Collection<Meta.ModelConfig.ModelInfo> values = modelInfoList.stream().collect(Collectors.toMap(Meta.ModelConfig.ModelInfo::getFieldName, o -> o, (e, r) -> r)).values();
        List<Meta.ModelConfig.ModelInfo> newModelInfoList = new ArrayList<>(values);
        return newModelInfoList;
    }

    public static void main(String[] args) {
        Meta meta = new Meta();
        meta.setName("acm-template-generator");
        meta.setDescription("ACM 示例模板生成器");
        String projectPath = System.getProperty("user.dir");
        //指定原始项目路径
        String originProjectPath = FileUtil.getAbsolutePath(new File(projectPath).getParentFile()) + File.separator + "jdjm-generator-demo-projects/springboot-init-master";
        String fileInputPath1 = "src/main/java/com/yupi/springbootinit/common";
        String fileInputPath2 = "src/main/java/com/yupi/springbootinit/controller";
        List<String> fileInputPathList = Arrays.asList(fileInputPath1,fileInputPath2);
        //输入模型参数
        Meta.ModelConfig.ModelInfo modelInfo = new Meta.ModelConfig.ModelInfo();
//        modelInfo.setFieldName("outputText");
        modelInfo.setType("String");
//        modelInfo.setDefaultValue("sum = ");
        String str = "Sum: ";

        modelInfo.setFieldName("className");
        str= "BaseResponse";
        TemplateMaker.makeTemplate(meta,originProjectPath,fileInputPathList,modelInfo,str,1808886735329832960L);
    }
}
