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
public class RenameDynamicSqlSupportPlugin extends PluginAdapter {
    private String replaceString;
    private Pattern pattern;
    @Override
    public boolean validate(List<String> warnings) {
        this.replaceString = this.properties.getProperty("replaceString");
        boolean valid = StringUtility.stringHasValue(this.replaceString);
        if (valid) {
            this.pattern = Pattern.compile("DynamicSqlSupport");
        } else {
            if (!StringUtility.stringHasValue(this.replaceString)) {
                warnings.add(Messages.getString("ValidationError.18", "RenameExampleClassPlugin", "replaceString"));
            }
        }
        return valid;
    }

    @Override
    public void initialized(IntrospectedTable introspectedTable) {
        // 更改实体类名称，例如：Menu => MenuDto
        //String oldType = introspectedTable.getBaseRecordType();
        //introspectedTable.setBaseRecordType(oldType + "Dto");

        // 更改DynamicSqlSupport名称
        String mapperType = introspectedTable.getMyBatisDynamicSqlSupportType();
        Matcher matcher = this.pattern.matcher(mapperType);
        introspectedTable.setMyBatisDynamicSqlSupportType(matcher.replaceAll(this.replaceString));
    }
}
