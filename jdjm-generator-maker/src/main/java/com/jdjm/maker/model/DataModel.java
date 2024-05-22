package com.jdjm.maker.model;

import lombok.Data;

/**
 * @author jdjm
 */
@Data
public class DataModel {

    /**
     * 是否生成 .gitignore文件
     */
    public boolean needGit = true;

    /**
     * 是否生成循环
     */
    public boolean loop = false;

    /**
     * 作者注释
     */
    private String author = "jdjm";

    /**
     * 输出信息
     */
    private String outputText = "sum = ";
}
