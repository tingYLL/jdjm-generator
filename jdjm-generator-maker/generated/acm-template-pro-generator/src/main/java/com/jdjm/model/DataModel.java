package com.jdjm.model;
import lombok.Data;

/**
* 数据模型
*/
@Data
public class DataModel {

    //有分组
    /**
     * 是否生成 .gitignore 文件
     */
    public boolean needGit = true;
    //有分组
    /**
     * 是否生成循环
     */
    public boolean loop = false;
    //有分组
    /**
    *核心模板
    */
    public MainTemplate mainTemplate = new MainTemplate();

    /**
    *用于生成核心模板文件MainTemplate
    */
    @Data
    public static class MainTemplate{
        /**
         * 作者注释
         */
        public String author = "jdjm";
        /**
         * 输出信息
         */
        public String outputText = "sum = ";
    }
}