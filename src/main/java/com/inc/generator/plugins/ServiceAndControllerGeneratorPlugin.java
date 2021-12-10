package com.inc.generator.plugins;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.internal.util.JavaBeansUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;

/**
 * @author tzb
 * @desc
 * @date 2021-12-10 22:23:10
 */
public class ServiceAndControllerGeneratorPlugin extends PluginAdapter {
    // 项目目录，一般为 src/main/java
    private String targetProject;

    // service包名，如：com.thinkj2ee.cms.service.service
    private String servicePackage;

    // service实现类包名，如：com.thinkj2ee.cms.service.service.impl
    private String serviceImplPackage;
    // Controlle类包名，如：com.thinkj2ee.cms.service.controller
    private String controllerPackage;
    // service接口名前缀
    private String servicePreffix;

    // service接口名后缀
    private String serviceSuffix;

    // service接口的父接口
    private String superServiceInterface;

    // service实现类的父类
    private String superServiceImpl;
    // controller类的父类
    private String superController;

    // dao接口基类
    private String superDaoInterface;

    // Example类的包名
    private String examplePacket;

    private String recordType;

    private String modelName;

    private FullyQualifiedJavaType model;

    private String serviceName;
    private String serviceImplName;
    private String controllerName;
    private String author;

    @Override
    public boolean validate(List<String> warnings) {
        boolean valid = true;

       /* if (!stringHasValue(properties
                .getProperty("targetProject"))) { //$NON-NLS-1$
            warnings.add(getString("ValidationError.18", //$NON-NLS-1$
                    "MapperConfigPlugin", //$NON-NLS-1$
                    "targetProject")); //$NON-NLS-1$
            valid = false;
        }
        if (!stringHasValue(properties.getProperty("servicePackage"))) { //$NON-NLS-1$
            warnings.add(getString("ValidationError.18", //$NON-NLS-1$
                    "MapperConfigPlugin", //$NON-NLS-1$
                    "servicePackage")); //$NON-NLS-1$
            valid = false;
        }
        if (!stringHasValue(properties.getProperty("serviceImplPackage"))) { //$NON-NLS-1$
            warnings.add(getString("ValidationError.18", //$NON-NLS-1$
                    "MapperConfigPlugin", //$NON-NLS-1$
                    "serviceImplPackage")); //$NON-NLS-1$
            valid = false;
        }
*/
        targetProject = properties.getProperty("targetProject");
        servicePackage = properties.getProperty("servicePackage");
        serviceImplPackage = properties.getProperty("serviceImplPackage");
        servicePreffix = properties.getProperty("servicePreffix");
        servicePreffix = stringHasValue(servicePreffix) ? servicePreffix : "";
        serviceSuffix = properties.getProperty("serviceSuffix");
        serviceSuffix = stringHasValue(serviceSuffix) ? serviceSuffix : "";
        superServiceInterface = properties.getProperty("superServiceInterface");
        superServiceImpl = properties.getProperty("superServiceImpl");
        superDaoInterface = properties.getProperty("superDaoInterface");
        controllerPackage = properties.getProperty("controllerPackage");
        superController = properties.getProperty("superController");
        author = properties.getProperty("author");

        return valid;
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {
        recordType = introspectedTable.getBaseRecordType();
        modelName = recordType.substring(recordType.lastIndexOf(".") + 1);
        model = new FullyQualifiedJavaType(recordType);
        serviceName = servicePackage + "." + servicePreffix + modelName + serviceSuffix;
        serviceImplName = serviceImplPackage + "." + modelName + serviceSuffix+"Impl";
        examplePacket=recordType.substring(0,recordType.lastIndexOf("."));
        controllerName=controllerPackage.concat(".").concat(modelName).concat("Controller");
        List<GeneratedJavaFile> answer = new ArrayList<>();
        GeneratedJavaFile gjf = generateServiceInterface(introspectedTable);
        GeneratedJavaFile gjf2 = generateServiceImpl(introspectedTable);
        GeneratedJavaFile gjf3 = generateController(introspectedTable);
        answer.add(gjf);
        answer.add(gjf2);
        answer.add(gjf3);
        return answer;
    }

    // 生成service接口
    private GeneratedJavaFile generateServiceInterface(IntrospectedTable introspectedTable) {
        String remarks = introspectedTable.getRemarks();
        FullyQualifiedJavaType service = new FullyQualifiedJavaType(serviceName);
        Interface serviceInterface = new Interface(service);
        serviceInterface.addJavaDocLine("/**");
        serviceInterface.addJavaDocLine(" * " + remarks + "管理 service");
        serviceInterface.addJavaDocLine(" * @author " + author);
        serviceInterface.addJavaDocLine(" * @date " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        serviceInterface.addJavaDocLine("*/");

        serviceInterface.setVisibility(JavaVisibility.PUBLIC);
        serviceInterface.addImportedType(new FullyQualifiedJavaType("com.github.pagehelper.PageInfo"));
        serviceInterface.addImportedType(new FullyQualifiedJavaType("com.inc.admin.domain.biz.Book"));
        serviceInterface.addImportedType(new FullyQualifiedJavaType("java.util.List"));
        FullyQualifiedJavaType returnListType = new FullyQualifiedJavaType("com.github.pagehelper.PageInfo");
        FullyQualifiedJavaType dtoType = introspectedTable.getRules().calculateAllFieldsClass();
        returnListType.addTypeArgument(dtoType);
        Parameter parameter = new Parameter(dtoType, "req");
        //查询列表
        addServiceMethod(serviceInterface,
                "listByPage",
                parameter,
                "分页查询",
                returnListType);
        //获取list
        returnListType = new FullyQualifiedJavaType("java.util.List");
        returnListType.addTypeArgument(dtoType);
        addServiceMethod(serviceInterface,
                "getList",
                parameter,
                "查询列表",
                returnListType);
        //单个查询
        addServiceMethod(serviceInterface,
                "getOne",
                parameter,
                "单个查询",
                dtoType);
        //新增
        addServiceMethod(serviceInterface,
                "insert",
                parameter,
                "新增",
                new FullyQualifiedJavaType("int"));
        //根据主键修改
        addServiceMethod(serviceInterface,
                "update",
                parameter,
                "根据主键修改",
                new FullyQualifiedJavaType("int"));
        //根据主键删除
        parameter = new Parameter(new FullyQualifiedJavaType("java.lang.Integer"), "id");
        addServiceMethod(serviceInterface,
                "delete",
                parameter,
                "根据主键删除",
                new FullyQualifiedJavaType("int"));

        GeneratedJavaFile gjf = new GeneratedJavaFile(serviceInterface, targetProject, context.getJavaFormatter());
        return gjf;
    }

    // 生成serviceImpl实现类
    private GeneratedJavaFile generateServiceImpl(IntrospectedTable introspectedTable) {
        FullyQualifiedJavaType service = new FullyQualifiedJavaType(serviceName);
        FullyQualifiedJavaType serviceImpl = new FullyQualifiedJavaType(serviceImplName);
        TopLevelClass clazz = new TopLevelClass(serviceImpl);
        //描述类的作用域修饰符
        clazz.setVisibility(JavaVisibility.PUBLIC);
        //描述类 引入的类
        clazz.addImportedType(service);
        //描述类 的实现接口类
        clazz.addSuperInterface(service);
        if(stringHasValue(superServiceImpl)) {
            String superServiceImplName = superServiceImpl.substring(superServiceImpl.lastIndexOf(".") + 1);
            clazz.addImportedType(superServiceImpl);
            clazz.addImportedType(recordType);
            clazz.setSuperClass(superServiceImplName + "<" + modelName + ">");
        }
        clazz.addImportedType(new FullyQualifiedJavaType("org.springframework.stereotype.Service"));
        //分页插件
        clazz.addImportedType(new FullyQualifiedJavaType("com.github.pagehelper.PageHelper"));
        //分页插件
        clazz.addImportedType(new FullyQualifiedJavaType("com.github.pagehelper.PageInfo"));
        //字符util非空判断
        clazz.addImportedType(new FullyQualifiedJavaType("org.apache.commons.lang3.StringUtils"));
        //转sql语句-start
        clazz.addImportedType(new FullyQualifiedJavaType("org.mybatis.dynamic.sql.SqlBuilder"));
        clazz.addImportedType(new FullyQualifiedJavaType("org.mybatis.dynamic.sql.select.QueryExpressionDSL"));
        clazz.addImportedType(new FullyQualifiedJavaType("org.mybatis.dynamic.sql.select.SelectDSLCompleter"));
        clazz.addImportedType(new FullyQualifiedJavaType("org.mybatis.dynamic.sql.util.Buildable"));
        clazz.addImportedType(new FullyQualifiedJavaType("org.springframework.stereotype.Service"));
        //转sql语句-end

        clazz.addImportedType(new FullyQualifiedJavaType("java.util.List"));
        clazz.addImportedType(new FullyQualifiedJavaType("java.util.Optional"));
        clazz.addAnnotation("@Service(\"" + firstCharToLowCase(modelName) + "Service\")");

        String daoFieldType = introspectedTable.getMyBatis3JavaMapperType();
        String daoFieldName = firstCharToLowCase(daoFieldType.substring(daoFieldType.lastIndexOf(".") + 1));
        //描述类的成员属性
        Field daoField = new Field(daoFieldName, new FullyQualifiedJavaType(daoFieldType));
        clazz.addImportedType(new FullyQualifiedJavaType(daoFieldType));
        clazz.addImportedType(new FullyQualifiedJavaType("javax.annotation.Resource"));
        //描述成员属性 的注解
        daoField.addAnnotation("@Resource");
        //描述成员属性修饰符
        daoField.setVisibility(JavaVisibility.PRIVATE);
        clazz.addField(daoField);
        FullyQualifiedJavaType returnListType = new FullyQualifiedJavaType("com.github.pagehelper.PageInfo");
        FullyQualifiedJavaType dtoType = introspectedTable.getRules().calculateAllFieldsClass();
        clazz.addImportedType(dtoType);
        returnListType.addTypeArgument(dtoType);
        Parameter parameter = new Parameter(dtoType, "req");
        StringBuffer bodyLineSB = new StringBuffer();
        //查询列表
        addServiceImplMethod(clazz,
                "listByPage",
                parameter,
                "分页查询",
                returnListType,
                bodyLineSB,
                JavaVisibility.PUBLIC);
        //构建查询条件
        returnListType = new FullyQualifiedJavaType("org.mybatis.dynamic.sql.select.SelectDSLCompleter");
        bodyLineSB = new StringBuffer();
        bodyLineSB.append("SelectDSLCompleter completer = new SelectDSLCompleter() {\n" +
                "            @Override\n" +
                "            public Buildable<SelectModel> apply(QueryExpressionDSL<SelectModel> selectModelQueryExpressionDSL) {\n" +
                "                QueryExpressionDSL<SelectModel>.QueryExpressionWhereBuilder where = selectModelQueryExpressionDSL.where();\n");
        List<IntrospectedColumn> allColumns = introspectedTable.getAllColumns();
        for (IntrospectedColumn column : allColumns) {
            String javaProperty = column.getJavaProperty();
            String javaTypeShortName = column.getFullyQualifiedJavaType().getShortName();
            FullyQualifiedJavaType javaType = column.getFullyQualifiedJavaType();
            String getName = JavaBeansUtil.getGetterMethodName(javaProperty, javaType);
            if (javaTypeShortName.equals("String")) {
                bodyLineSB.append(
                                "                String "+ javaProperty + " = req." + getName + "();\n" +
                                "                if (StringUtils.isNotBlank(" + javaProperty + ")) {\n" +
                                "                    where.and(" + modelName + "Sql." + javaProperty + ", SqlBuilder.isEqualTo(req." + getName + "()));\n" +
                                "                }\n");
            } else {
                bodyLineSB.append(
                                "                " + javaType.getShortName() + " "+ javaProperty + " = req." + getName + "();\n" +
                                "                if (" + javaProperty + " != null) {\n" +
                                "                    where.and(" + modelName + "Sql." + javaProperty + ", SqlBuilder.isEqualTo(req." + getName + "()));\n" +
                                "                }\n");
            }

        }
        bodyLineSB.append(
                "                return where;\n" +
                "            }\n" +
                "        };\n" +
                "        return completer;");
        addServiceImplMethod(clazz,
                "buildCompleter",
                parameter,
                "构建查询条件",
                returnListType,
                bodyLineSB,
                JavaVisibility.PRIVATE);

        GeneratedJavaFile gjf2 = new GeneratedJavaFile(clazz, targetProject, context.getJavaFormatter());
        return gjf2;
    }


    // 生成controller类
    private GeneratedJavaFile generateController(IntrospectedTable introspectedTable) {
        String remarks = introspectedTable.getRemarks();
        FullyQualifiedJavaType controller = new FullyQualifiedJavaType(controllerName);
        TopLevelClass clazz = new TopLevelClass(controller);
        //描述类的作用域修饰符
        clazz.setVisibility(JavaVisibility.PUBLIC);

        //实体类
        FullyQualifiedJavaType dtoType = introspectedTable.getRules().calculateAllFieldsClass();
        clazz.addImportedType(dtoType);
        clazz.addImportedType(new FullyQualifiedJavaType("com.inc.admin.utils.R"));
        clazz.addImportedType(new FullyQualifiedJavaType("org.springframework.validation.annotation.Validated"));
        clazz.addImportedType(new FullyQualifiedJavaType("org.springframework.web.bind.annotation.*"));
        clazz.addImportedType(new FullyQualifiedJavaType("javax.annotation.Resource"));
        clazz.addImportedType(new FullyQualifiedJavaType("javax.validation.constraints.NotNull"));

        //添加@Controller注解，并引入相应的类
        clazz.addAnnotation("@RestController");
        //添加@RequestMapping注解，并引入相应的类
        clazz.addAnnotation("@RequestMapping(\"/"+firstCharToLowCase(modelName)+"\")");
        clazz.addJavaDocLine("/**");
        clazz.addJavaDocLine(" * " + remarks + "管理 控制器");
        clazz.addJavaDocLine(" * @author " + author);
        clazz.addJavaDocLine(" * @date " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        clazz.addJavaDocLine("*/");

        //引入controller的父类和model，并添加泛型
        if(stringHasValue(superController)) {
            clazz.addImportedType(superController);
            clazz.addImportedType(recordType);
            FullyQualifiedJavaType superInterfac = new FullyQualifiedJavaType(superController+"<"+modelName+">");
            clazz.addSuperInterface(superInterfac);
        }

        //添加Service成员变量
        String serviceFieldName = firstCharToLowCase(serviceName.substring(serviceName.lastIndexOf(".") + 1));
        Field daoField = new Field(serviceFieldName, new FullyQualifiedJavaType(serviceName));
        clazz.addImportedType(new FullyQualifiedJavaType(serviceName));
        clazz.addImportedType(new FullyQualifiedJavaType("javax.annotation.Resource"));


        //描述成员属性 的注解
        daoField.addAnnotation("@Resource");
        //描述成员属性修饰符
        daoField.setVisibility(JavaVisibility.PRIVATE);
        clazz.addField(daoField);

        StringBuffer bodyLineSB = new StringBuffer();
        //分页查询
        bodyLineSB.append("return R.ok().put(\"page\", ");
        bodyLineSB.append(serviceFieldName + ".listByPage(req));");
        Parameter parameter = new Parameter(dtoType, "req");
        parameter.addAnnotation("@RequestBody");
        addControllerMethod(clazz,
                "listByPage",
                parameter,
                "分页查询 " + remarks + "列表",
                new FullyQualifiedJavaType("R"),
                bodyLineSB);
        //插入
        bodyLineSB = new StringBuffer();
        bodyLineSB.append("return R.operate(");
        bodyLineSB.append(serviceFieldName + ".insert(req)>0);");
        addControllerMethod(clazz,
                "insert",
                parameter,
                "添加 " + remarks + "信息",
                new FullyQualifiedJavaType("R"),
                bodyLineSB);
        //更新
        bodyLineSB = new StringBuffer();
        bodyLineSB.append("return R.operate(");
        bodyLineSB.append(serviceFieldName + ".update(req)>0);");
        addControllerMethod(clazz,
                "update",
                parameter,
                "更新 " + remarks + "信息",
                new FullyQualifiedJavaType("R"),
                bodyLineSB);
        //删除
        parameter = new Parameter(new FullyQualifiedJavaType("java.lang.Integer"), "id");
        parameter.addAnnotation("@Validated");
        parameter.addAnnotation("@NotNull(message = \"编号不能为空\")");
        parameter.addAnnotation("@RequestParam(\"id\")");
        parameter.addAnnotation("@RequestBody");
        bodyLineSB = new StringBuffer();
        bodyLineSB.append("return R.operate(");
        bodyLineSB.append(serviceFieldName + ".delete(id)>0);");
        addControllerMethod(clazz,
                "delete",
                parameter,
                "删除 " + remarks + "信息",
                new FullyQualifiedJavaType("R"),
                bodyLineSB);
        GeneratedJavaFile gjf2 = new GeneratedJavaFile(clazz, targetProject, context.getJavaFormatter());
        return gjf2;
    }

    private void addControllerMethod(TopLevelClass clazz, String methodName, Parameter parameter, String methodRemark, FullyQualifiedJavaType returnType, StringBuffer bodyLineSB) {
        Method method = new Method(methodName);
        method.addAnnotation("@PostMapping(\"/"+methodName+"\")");
        //方法参数
        method.addParameter(parameter);
        //方法注解
        method.addJavaDocLine("/**");
        method.addJavaDocLine(" * "+ methodRemark);
        method.addJavaDocLine(" */");
        //返回类型
        method.setReturnType(returnType);
        method.addBodyLine(bodyLineSB.toString());
        //修饰符
        method.setVisibility(JavaVisibility.PUBLIC);
        clazz.addMethod(method);
    }

    private void addServiceImplMethod(TopLevelClass clazz, String methodName, Parameter parameter, String methodRemark, FullyQualifiedJavaType returnType, StringBuffer bodyLineSB, JavaVisibility visibility) {
        Method method = new Method(methodName);
        if (visibility.equals(JavaVisibility.PUBLIC)) {
            method.addAnnotation("@Override");
        }
        //方法参数
        method.addParameter(parameter);
        //方法注解
        method.addJavaDocLine("/**");
        method.addJavaDocLine(" * "+ methodRemark);
        method.addJavaDocLine(" */");
        //返回类型
        method.setReturnType(returnType);
        method.addBodyLine(bodyLineSB.toString());
        //修饰符
        method.setVisibility(visibility);
        clazz.addMethod(method);
    }

    private void addServiceMethod(Interface serviceInterface, String methodName, Parameter parameter, String methodRemark, FullyQualifiedJavaType returnType) {
        Method method = new Method(methodName);
        method.setAbstract(true);
        //方法参数
        method.addParameter(parameter);
        //方法注解
        method.addJavaDocLine("/**");
        method.addJavaDocLine(" * "+ methodRemark);
        method.addJavaDocLine(" */");
        //返回类型
        method.setReturnType(returnType);
        serviceInterface.addMethod(method);
    }


    private String firstCharToLowCase(String str) {
        char[] chars = new char[1];
        chars[0] = str.charAt(0);
        String temp = new String(chars);
        if(chars[0] >= 'A'  &&  chars[0] <= 'Z') {
            return str.replaceFirst(temp,temp.toLowerCase());
        }
        return str;
    }
}
