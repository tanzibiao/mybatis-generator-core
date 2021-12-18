package com.inc.generator.plugins;

import org.apache.commons.io.IOUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.internal.util.StringUtility;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

/**
 * 生成vue代码
 *
 */
public class VueGenerator extends PluginAdapter {

    @Override
    public boolean validate(List<String> list) {
        return true;
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {
        gen(introspectedTable);
        List<GeneratedJavaFile> answer = new ArrayList<>();

        return answer;
    }

    public void gen(IntrospectedTable introspectedTable) {
        //基础类
        FullyQualifiedJavaType modelType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        Map<String, Object> map = new HashMap<>(16);
        map.put("modelName", getFirstLowerCaseWord(modelType.getShortName()));
        map.put("apiFile", modelType.getShortName());
        map.put("comment", introspectedTable.getRemarks());
        map.put("pk", introspectedTable.getPrimaryKeyColumns().get(0).getJavaProperty());
        map.put("baseColumns", introspectedTable.getBaseColumns());
        VelocityContext context = new VelocityContext(map);
        Properties properties=new Properties();
        //设置velocity资源加载方式为class
        properties.setProperty("resource.loader", "class");
        //设置velocity资源加载方式为file时的处理类
        properties.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");

        VelocityEngine ve = new VelocityEngine(properties);
        //设置参数
        //  ve.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, templatepath);
        //处理中文问题
        ve.setProperty(Velocity.INPUT_ENCODING,"utf-8");
        ve.setProperty(Velocity.OUTPUT_ENCODING,"utf-8");
        //初始化
        ve.init();
        // 获取模板列表
        List<String> templates = getTemplates();
        for (String template : templates)
        {
            // 渲染模板
            StringWriter sw = new StringWriter();
            Template tpl = ve.getTemplate(template, "UTF-8");
            tpl.merge(context, sw);

            try
            {
                // 添加到zip
                String basePath = "src/generate/resources/js/";
                String path = basePath + "view/" + getFirstLowerCaseWord(modelType.getShortName())+"/";
                String fileName = "list.vue";
                if (template.contains("api")) {
                    path = basePath + "api/";
                    fileName = "api"+modelType.getShortName()+".js";
                } else if (template.contains("addForm")){
                    fileName = "addForm.vue";
                    path = path+"components/";
                }
                File file = new File(path);
                if (!file.exists())
                {
                    file.mkdirs();
                }
                FileOutputStream outFile = new FileOutputStream(path+fileName);
                IOUtils.write(sw.toString(), outFile, "UTF-8");
                IOUtils.closeQuietly(sw);
                outFile.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public static List<String> getTemplates()
    {
        List<String> templates = new ArrayList<String>();
        templates.add("templates/api.js.vm");
        templates.add("templates/list.vue.vm");
        templates.add("templates/addForm.vue.vm");
        return templates;
    }

    public static String getFirstLowerCaseWord(String word) {
        String firstWord = word.substring(0, 1).toLowerCase();
        String endWord = word.substring(1);
        return firstWord + endWord;
    }
}
