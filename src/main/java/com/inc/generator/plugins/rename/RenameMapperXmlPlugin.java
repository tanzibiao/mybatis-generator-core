package com.inc.generator.plugins.rename;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.internal.util.StringUtility;
import org.mybatis.generator.internal.util.messages.Messages;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author tzb
 * @desc 重命名文件
 * @date 2021-12-10 20:17:23
 */
public class RenameMapperXmlPlugin extends PluginAdapter {
    private String replaceString;
    private Pattern pattern;
    @Override
    public boolean validate(List<String> warnings) {
        this.replaceString = this.properties.getProperty("replaceString");
        boolean valid = StringUtility.stringHasValue(this.replaceString);
        if (valid) {
            this.pattern = Pattern.compile("Mapper");
        } else {
            if (!StringUtility.stringHasValue(this.replaceString)) {
                warnings.add(Messages.getString("ValidationError.18", "RenameExampleClassPlugin", "replaceString"));
            }
        }
        return valid;
    }

    @Override
    public void initialized(IntrospectedTable introspectedTable) {
        String mapperType = introspectedTable.getMyBatis3XmlMapperFileName();
        Matcher matcher = this.pattern.matcher(mapperType);
        introspectedTable.setMyBatis3XmlMapperFileName(matcher.replaceAll(this.replaceString));
    }
}
