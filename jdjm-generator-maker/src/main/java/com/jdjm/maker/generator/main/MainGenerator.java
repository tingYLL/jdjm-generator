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

public class MainGenerator extends GenerateTemplate{
    public static void main(String[] args) throws TemplateException, IOException, InterruptedException {
        MainGenerator mainGenerator = new MainGenerator();
        mainGenerator.doGenerate();
    }

    //我们使用了模版方法，这样的做的好处是可扩展性强，比如我们不想要生成精简版的程序，只需要重写buildDist
    @Override
    protected void buildDist(String outputPath, String sourceCopyDestPath, String shellOutputFilePath, String jarPath) {
        System.out.println("不生成精简版程序");
    }
}
