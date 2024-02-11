package com.jdjm.maker.generator;

import java.io.*;

/**
 * 程序构建jar包
 */
public class JarGenerator {
    public static void doGenerator(String projectDir) throws IOException, InterruptedException {
        String winMavenCommand = "mvn.cmd clean package -DskipTest=true";
        String otherMavenCommand = "mvn clean package -DskipTest=true";
        String mavenCommand = winMavenCommand;

        ProcessBuilder processBuilder = new ProcessBuilder(mavenCommand.split(" "));
        processBuilder.directory(new File(projectDir));

        //相当于开启一个终端
        Process process = processBuilder.start();

        InputStream inputStream = process.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        //读取缓冲区的每一行
        while((line = bufferedReader.readLine())!=null){
            //输出构建信息
            System.out.println(line);
        }

        int exitCode = process.waitFor();
        System.out.println("命令执行结束,退出码:"+exitCode);

    }
}
