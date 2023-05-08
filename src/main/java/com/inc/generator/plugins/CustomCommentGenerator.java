package com.inc.generator.plugins;

import org.apache.commons.lang.StringUtils;
import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.kotlin.KotlinFile;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.config.MergeConstants;
import org.mybatis.generator.config.PropertyRegistry;
import org.mybatis.generator.internal.util.StringUtility;

import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.mybatis.generator.internal.util.StringUtility.isTrue;


public class CustomCommentGenerator implements CommentGenerator {

    private Properties properties;

    private boolean suppressDate;

    private boolean suppressAllComments;

    /** If suppressAllComments is true, this option is ignored. */
    private boolean addRemarkComments;

    private SimpleDateFormat dateFormat;

    public CustomCommentGenerator() {
        super();
        properties = new Properties();
        suppressDate = false;
        suppressAllComments = false;
        addRemarkComments = false;
    }

    @Override
    public void addJavaFileComment(CompilationUnit compilationUnit) {
        // add no file level comments by default
    }

    /**
     * Adds a suitable comment to warn users that the element was generated, and
     * when it was generated.
     *
     * @param xmlElement the xml element
     */
    @Override
    public void addComment(XmlElement xmlElement) {
        if (suppressAllComments) {
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(MergeConstants.NEW_ELEMENT_TAG);
        xmlElement.addElement(new TextElement("<!-- " + sb.toString() + " -->"));

    }

    @Override
    public void addRootComment(XmlElement rootElement) {
        // add no document level comments by default
    }

    @Override
    public void addConfigurationProperties(Properties properties) {
        this.properties.putAll(properties);

        suppressDate = isTrue(properties.getProperty(PropertyRegistry.COMMENT_GENERATOR_SUPPRESS_DATE));

        suppressAllComments = isTrue(properties.getProperty(PropertyRegistry.COMMENT_GENERATOR_SUPPRESS_ALL_COMMENTS));

        addRemarkComments = isTrue(properties.getProperty(PropertyRegistry.COMMENT_GENERATOR_ADD_REMARK_COMMENTS));

        String dateFormatString = properties.getProperty(PropertyRegistry.COMMENT_GENERATOR_DATE_FORMAT);
        if (StringUtility.stringHasValue(dateFormatString)) {
            dateFormat = new SimpleDateFormat(dateFormatString);
        }
    }

    /**
     * This method adds the custom javadoc tag for. You may do nothing if you do not
     * wish to include the Javadoc tag - however, if you do not include the Javadoc
     * tag then the Java merge capability of the eclipse plugin will break.
     *
     * @param javaElement       the java element
     * @param markAsDoNotDelete the mark as do not delete
     */
    protected void addJavadocTag(JavaElement javaElement, boolean markAsDoNotDelete) {
        javaElement.addJavaDocLine(" *"); //$NON-NLS-1$
        StringBuilder sb = new StringBuilder();
        sb.append(" * "); //$NON-NLS-1$
        sb.append(MergeConstants.NEW_ELEMENT_TAG);
        javaElement.addJavaDocLine(sb.toString());
    }

    /**
     * Returns a formated date string to include in the Javadoc tag and XML
     * comments. You may return null if you do not want the date in these
     * documentation elements.
     *
     * @return a string representing the current timestamp, or null
     */
    protected String getDateString() {
        if (suppressDate) {
            return null;
        } else if (dateFormat != null) {
            return dateFormat.format(new Date());
        } else {
            return new Date().toString();
        }
    }

    @Override
    public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable) {
        if (suppressAllComments) {
            return;
        }
        innerClass.addJavaDocLine("/**"); //$NON-NLS-1$
        innerClass.addJavaDocLine(" * ");
        String remarks = introspectedTable.getRemarks();
        if (addRemarkComments && StringUtility.stringHasValue(remarks)) {
            String[] remarkLines = remarks.split(System.getProperty("line.separator")); //$NON-NLS-1$
            for (String remarkLine : remarkLines) {
                innerClass.addJavaDocLine(" * <p>" + remarkLine); //$NON-NLS-1$
            }
        }
        innerClass.addJavaDocLine(" * @author " + properties.getProperty("author", "sys"));
        innerClass.addJavaDocLine(" * @date " + getDateString());


        innerClass.addJavaDocLine(" */"); //$NON-NLS-1$
    }

    @Override
    public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable, boolean markAsDoNotDelete) {
        addClassComment(innerClass, introspectedTable);
    }

    @Override
    public void addModelClassComment(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        if (suppressAllComments) {
            return;
        }
        topLevelClass.addJavaDocLine("/**"); //$NON-NLS-1$
        topLevelClass.addJavaDocLine(" * ");
        topLevelClass.addJavaDocLine(" * 表名：" + introspectedTable.getTableConfiguration().getTableName());
        String remarks = introspectedTable.getRemarks();
        if (addRemarkComments && StringUtility.stringHasValue(remarks)) {
            String[] remarkLines = remarks.split(System.getProperty("line.separator")); //$NON-NLS-1$
            for (String remarkLine : remarkLines) {
                topLevelClass.addJavaDocLine(" * " + remarkLine); //$NON-NLS-1$
            }
        }
        topLevelClass.addJavaDocLine(" * @author " + properties.getProperty("author", "sys"));
        topLevelClass.addJavaDocLine(" * @date " + getDateString());


        topLevelClass.addJavaDocLine(" */"); //$NON-NLS-1$
    }

    @Override
    public void addEnumComment(InnerEnum innerEnum, IntrospectedTable introspectedTable) {
        if (suppressAllComments) {
            return;
        }
        innerEnum.addJavaDocLine("/**"); //$NON-NLS-1$
        innerEnum.addJavaDocLine(" * ");
        String remarks = introspectedTable.getRemarks();
        if (addRemarkComments && StringUtility.stringHasValue(remarks)) {
            String[] remarkLines = remarks.split(System.getProperty("line.separator")); //$NON-NLS-1$
            for (String remarkLine : remarkLines) {
                innerEnum.addJavaDocLine(" * " + remarkLine); //$NON-NLS-1$
            }
        }
        innerEnum.addJavaDocLine(" * @author " + properties.getProperty("author", "sys"));
        innerEnum.addJavaDocLine(" * @date " + getDateString());


        innerEnum.addJavaDocLine(" */"); //$NON-NLS-1$
    }

    @Override
    public void addFieldComment(Field field, IntrospectedTable introspectedTable,
                                IntrospectedColumn introspectedColumn) {
        if (suppressAllComments) {
            return;
        }



        String remarks = introspectedColumn.getRemarks();
        if (addRemarkComments && StringUtility.stringHasValue(remarks)) {
            String[] remarkLines = remarks.split(System.getProperty("line.separator")); //$NON-NLS-1$
            if (remarkLines.length > 1) {
                field.addJavaDocLine("/**"); //$NON-NLS-1$
                for (String remarkLine : remarkLines) {
                    field.addJavaDocLine(" * " + remarkLine); //$NON-NLS-1$
                }
                field.addJavaDocLine(" */"); //$NON-NLS-1$
            } else {
                //只有1行注释，则不换行，代码更简洁
                field.addJavaDocLine("/**" + remarks + "*/"); //$NON-NLS-1$
            }

        }


    }

    @Override
    public void addFieldComment(Field field, IntrospectedTable introspectedTable) {
    }

    @Override
    public void addGeneralMethodComment(Method method, IntrospectedTable introspectedTable) {
        if (suppressAllComments) {
            return;
        }


        method.addJavaDocLine("/**"); //$NON-NLS-1$
        String methodName = method.getName();
        if (methodName.startsWith("set")) {
            String remarks = getColRemarks(methodName.replaceFirst("set", ""), introspectedTable);
            if (StringUtils.isNotBlank(remarks)) {
                method.addJavaDocLine(" * 设置 " + remarks);
            }
        } else if (methodName.startsWith("get")) {
            String remarks = getColRemarks(methodName.replaceFirst("get", ""), introspectedTable);
            if (StringUtils.isNotBlank(remarks)) {
                method.addJavaDocLine(" * 获取 " + remarks);
            }
        } else if (methodName.equals("count")) {
            method.addJavaDocLine(" * 查询 " + introspectedTable.getRemarks() + " 数量");
        } else if (methodName.equals("update")) {
            method.addJavaDocLine(" * 更新 " + introspectedTable.getRemarks());
        } else if (methodName.equals("delete")) {
            method.addJavaDocLine(" * 删除 " + introspectedTable.getRemarks());
        } else if (methodName.equals("insert")) {
            method.addJavaDocLine(" * 添加 " + introspectedTable.getRemarks());
        } else if (methodName.equals("insertMultiple")) {
            method.addJavaDocLine(" * 批量添加 " + introspectedTable.getRemarks());
        } else if (methodName.equals("selectOne")) {
            method.addJavaDocLine(" * 查询单条 " + introspectedTable.getRemarks());
        } else if (methodName.equals("selectMany")) {
            method.addJavaDocLine(" * 查询多条 " + introspectedTable.getRemarks());
        } else if (methodName.equals("deleteByPrimaryKey")) {
            method.addJavaDocLine(" * 根据主键删除 " + introspectedTable.getRemarks());
        } else if (methodName.equals("insertSelective")) {
            method.addJavaDocLine(" * 插入值不为空的列 ");
        } else if (methodName.equals("select")) {
            method.addJavaDocLine(" * 查询多条 " + introspectedTable.getRemarks());
        } else if (methodName.equals("selectDistinct")) {
            method.addJavaDocLine(" * 去重查询 " + introspectedTable.getRemarks());
        } else if (methodName.equals("selectByPrimaryKey")) {
            method.addJavaDocLine(" * 根据主键查询 " + introspectedTable.getRemarks());
        } else if (methodName.equals("updateAllColumns")) {
            method.addJavaDocLine(" * 更新所有列 ");
        } else if (methodName.equals("updateSelectiveColumns")) {
            method.addJavaDocLine(" * 更新值不为空的列 ");
        } else if (methodName.equals("updateByPrimaryKey")) {
            method.addJavaDocLine(" * 根据主键更新 " + introspectedTable.getRemarks());
        } else if (methodName.equals("updateByPrimaryKeySelective")) {
            method.addJavaDocLine(" * 根据主键更新值不为空的列 ");
        }
        List<Parameter> parameters = method.getParameters();
        for(Parameter parameter : parameters) {
            StringBuilder sb = new StringBuilder();
            sb.append(" * @param "); //$NON-NLS-1$
            sb.append(parameter.getName());
            method.addJavaDocLine(sb.toString());
        }

        Optional<FullyQualifiedJavaType> returnType = method.getReturnType();
        returnType.ifPresent(x -> method.addJavaDocLine(" * @return " + x.getFullyQualifiedName()));

        method.addJavaDocLine(" */"); //$NON-NLS-1$
    }

    private String getColRemarks(String colName, IntrospectedTable introspectedTable) {
        List<IntrospectedColumn> columns = introspectedTable.getAllColumns();
        for (IntrospectedColumn column : columns) {
            if (column.getJavaProperty().toUpperCase().equals(colName.toUpperCase())) {
                return column.getRemarks();
            }
        }
        return null;
    }

    @Override
    public void addGetterComment(Method method, IntrospectedTable introspectedTable,
                                 IntrospectedColumn introspectedColumn) {
    }

    @Override
    public void addSetterComment(Method method, IntrospectedTable introspectedTable,
                                 IntrospectedColumn introspectedColumn) {
    }

    @Override
    public void addGeneralMethodAnnotation(Method method, IntrospectedTable introspectedTable,
                                           Set<FullyQualifiedJavaType> imports) {
        addGeneralMethodComment(method, introspectedTable);
    }

    @Override
    public void addGeneralMethodAnnotation(Method method, IntrospectedTable introspectedTable,
                                           IntrospectedColumn introspectedColumn, Set<FullyQualifiedJavaType> imports) {
        addGeneralMethodComment(method, introspectedTable);
    }

    @Override
    public void addFieldAnnotation(Field field, IntrospectedTable introspectedTable,
                                   Set<FullyQualifiedJavaType> imports) {
        addFieldComment(field, introspectedTable);
    }

    @Override
    public void addFieldAnnotation(Field field, IntrospectedTable introspectedTable,
                                   IntrospectedColumn introspectedColumn, Set<FullyQualifiedJavaType> imports) {
        addFieldComment(field, introspectedTable, introspectedColumn);
    }

    @Override
    public void addClassAnnotation(InnerClass innerClass, IntrospectedTable introspectedTable,
                                   Set<FullyQualifiedJavaType> imports) {
    }

    @SuppressWarnings("unused")
    private String getGeneratedAnnotation(String comment) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("@Generated("); //$NON-NLS-1$
        if (suppressAllComments) {
            buffer.append('\"');
        } else {
            buffer.append("value=\""); //$NON-NLS-1$
        }

        buffer.append(MyBatisGenerator.class.getName());
        buffer.append('\"');

        if (!suppressDate && !suppressAllComments) {
            buffer.append(", date=\""); //$NON-NLS-1$
            buffer.append(DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(ZonedDateTime.now()));
            buffer.append('\"');
        }

        if (!suppressAllComments) {
            buffer.append(", comments=\""); //$NON-NLS-1$
            buffer.append(comment);
            buffer.append('\"');
        }

        buffer.append(')');
        return buffer.toString();
    }

    @Override
    public void addFileComment(KotlinFile kotlinFile) {
    }
}
