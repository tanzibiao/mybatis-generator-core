package com.inc.generator;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.api.VerboseProgressCallback;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * 生成代码类
 * 请单独打开该项目
 * @Author tanzibiao
 * @Date 2021-03-16 10:03:33
 **/
@Slf4j
public class Test {
    public static final ResourceBundle CONFIG_RESOURCE_BUNDLE = ResourceBundle.getBundle("config");

    public static void main(String[] args) throws Exception {

        /*
         * 1. 删除已生成过的文件
         */
        String path = System.getProperty("user.dir") + "/src/generate";
        File parent = new File(path+"/java");
        if (!parent.exists()) {
            parent.mkdirs();
        }
        Test.deleteFile(path);
        /*
         * 2. 初始化配置解析器
         */
        List<String> warnings = new ArrayList<>();
        ConfigurationParser cp = new ConfigurationParser(warnings);
        /*
         * 3. 调用配置解析器创建配置对象
         */
        String configPath = System.getProperty("user.dir")+CONFIG_RESOURCE_BUNDLE.getString("config.path");//Test.class.getResource("/risk/generatorConfig-pls.xml").getPath();
        System.out.println(configPath);
        File configFile = new File(configPath);
        Configuration config = cp.parseConfiguration(configFile);
        /*
         * 4. shellCallback接口主要用来处理生成的文件的创建、覆盖、合并，传入overwrite参数；默认的shellCallback是不支持文件合并的；
         */
        DefaultShellCallback callback = new DefaultShellCallback(true);
        /*
         * 5. 创建一个MyBatisGenerator对象。MyBatisGenerator类是真正用来执行生成动作的类
         */
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
        myBatisGenerator.generate(new VerboseProgressCallback());
        for (String warning : warnings) {
            System.out.println("警告：" + warning);
        }
    }

    /**
     * * 删除某个文件夹下的所有文件夹和文件 * @param path String * @throws
     * FileNotFoundException * @throws IOException * @return boolean
     */
    public static boolean deleteFile(String path){
        try {
            File file = new File(path);
            if (!file.isDirectory()) {
                if(!file.getName().contains("BaseEntity")){
                    boolean flag = file.delete();
                    System.out.println(file.getPath() + " 删除：" + flag);
                }
            } else if (file.isDirectory()) {
                String[] fileList = file.list();
                if(fileList != null){
                    for (String filepath:fileList) {
                        deleteFile(path + "/" + filepath);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("删除文件异常：" + e.getMessage());
        }
        return true;
    }
}
