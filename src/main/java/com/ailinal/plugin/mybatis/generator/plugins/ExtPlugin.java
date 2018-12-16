package com.ailinal.plugin.mybatis.generator.plugins;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.GeneratedXmlFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.DefaultJavaFormatter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.XmlConstants;
import org.mybatis.generator.config.TableConfiguration;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description: 生成额外的Mapper.java和Mapper.xml
 *
 * @author yang
 * @version 18-6-22
 */
public class ExtPlugin extends PluginAdapter {

    /**
     * 额外后缀，如（UsersMapper => Users + extSuffix + Mapper）
     */
    private String extSuffix;

    /**
     * 生成额外Mapper文件，xml文件存放的包名（原Mapper文件所在包下）
     */
    private String extPackageName;

    /**
     * mapper完整名称，包名+文件名
     */
    private String mapperCompleteName;

    /**
     * mapper文件名称
     */
    private String mapperName;

    /**
     * 额外的mapper包名称
     */
    private String mapperExtPackageName;

    /**
     * 额外的mapper文件名称
     */
    private String mapperExtName;

    /**
     * 额外的mapper文件的项目磁盘路径
     */
    private String mapperTargetProject;

    /**
     * 额外的mapper对应的xml文件包名称
     */
    private String xmlMapperExtPackageName;

    /**
     * 额外的mapper对应的xml文件名
     */
    private String xmlMapperExtName;

    /**
     * 额外的mapper对应的xml文件的项目磁盘路径
     */
    private String xmlMapperTargetProject;

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public void initialized(IntrospectedTable introspectedTable) {

        extSuffix = properties.getProperty("extSuffix");

        extPackageName = properties.getProperty("extPackageName");

        if (this.extSuffix == null || "".equals(this.extSuffix)) {
            this.extSuffix = "Ext";
        }
        if (this.extPackageName == null || "".equals(this.extPackageName)) {
            this.extPackageName = "ext";
        }
        if (mapperExtPackageName == null || "".equals(mapperExtPackageName)) {
            mapperExtPackageName = context.getJavaClientGeneratorConfiguration().getTargetPackage() + "." +extPackageName;
        }
        if (xmlMapperExtPackageName == null || "".equals(xmlMapperExtPackageName)) {
            xmlMapperExtPackageName = context.getSqlMapGeneratorConfiguration().getTargetPackage() + "." +extPackageName;
        }

        mapperCompleteName = introspectedTable.getMyBatis3JavaMapperType();
        String[] strings = mapperCompleteName.split("\\.");
        mapperName = strings[strings.length - 1];

        mapperTargetProject = context.getJavaClientGeneratorConfiguration().getTargetProject();
        xmlMapperTargetProject = context.getSqlMapGeneratorConfiguration().getTargetProject();
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles() {
        List<GeneratedJavaFile> files = new ArrayList<>();

        List<TableConfiguration> tableConfigurations = context.getTableConfigurations();
        if(tableConfigurations != null && tableConfigurations.size() != 0) {
            for(TableConfiguration tableConfiguration : tableConfigurations) {
                mapperExtName = buildName(tableConfiguration) + extSuffix + "Mapper";
                if("".equals(mapperExtName)) {
                    System.out.println("table name error");
                    continue;
                }
                if (isExtJavaFileExits()) {
                    System.out.println(mapperExtName + "java. exits do nothing.");
                    continue;
                }
                System.out.println("generate " + mapperExtName + ".java");
                files.add(generatedExtJavaFile());
            }
            return files;
        }
        return files;
    }

    @Override
    public List<GeneratedXmlFile> contextGenerateAdditionalXmlFiles() {
        List<GeneratedXmlFile> files = new ArrayList<>();
        List<TableConfiguration> tableConfigurations = context.getTableConfigurations();
        if(tableConfigurations != null && tableConfigurations.size() != 0) {
            for(TableConfiguration tableConfiguration : tableConfigurations) {
                xmlMapperExtName = buildName(tableConfiguration) + extSuffix + "Mapper.xml";
                if("".equals(xmlMapperExtName)) {
                    System.out.println("table name error");
                    continue;
                }
                if (isExtXmlFileExits()) {
                    System.out.println(xmlMapperExtName + "java. exits do nothing.");
                    continue;
                }
                System.out.println("generate " + xmlMapperExtName + ".java");
                files.add(generatedExtXmlFile());
            }
            return files;
        }
        return files;
    }

    @Override
    public boolean sqlMapGenerated(GeneratedXmlFile sqlMap, IntrospectedTable introspectedTable) {
        try {
            //反射改isMergeable的值
            //为false后，XXXMapper.xml文件不追加，变为覆盖
            Field mergeable = sqlMap.getClass().getDeclaredField("isMergeable");
            mergeable.setAccessible(true);
            mergeable.setBoolean(sqlMap,false);

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 生成Ext接口
     *
     * @return Mapper.java
     */
    private GeneratedJavaFile generatedExtJavaFile() {

        // 拼装接口
        Interface inter = new Interface(mapperExtPackageName + "." + mapperExtName);
        inter.addSuperInterface(new FullyQualifiedJavaType(mapperName));
        inter.setVisibility(JavaVisibility.PUBLIC);

        // 添加注解 与 注解引入
        inter.addAnnotation("@Mapper");
        inter.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Mapper"));
        inter.addImportedType(new FullyQualifiedJavaType(mapperCompleteName));

        return new GeneratedJavaFile(inter, mapperTargetProject, new DefaultJavaFormatter());
    }

    /**
     * 生成ExtXml
     *
     * @return Mapper.xml
     */
    private GeneratedXmlFile generatedExtXmlFile() {

        // 创建文档对象
        Document document = new Document(XmlConstants.MYBATIS3_MAPPER_PUBLIC_ID, XmlConstants.MYBATIS3_MAPPER_SYSTEM_ID);
        // 创建根节点
        XmlElement root = new XmlElement("mapper");
        root.addAttribute(new Attribute("namespace", mapperExtPackageName + "." + mapperExtName));
        root.addElement(new TextElement(""));
        document.setRootElement(root);

        // 生成ext xml文件
        return new GeneratedXmlFile(document, xmlMapperExtName, xmlMapperExtPackageName, xmlMapperTargetProject, false, context.getXmlFormatter());
    }

    private boolean isExtJavaFileExits() {
        return new File(mapperTargetProject + "/" + mapperExtPackageName.replace(".","/") + "/" +mapperExtName  + ".java").exists();
    }

    private boolean isExtXmlFileExits() {
        return new File(xmlMapperTargetProject + "/" + xmlMapperExtPackageName.replace('.', '/') + "/" + xmlMapperExtName).exists();
    }


    /**
     * 首字符大写
     * @param str
     * @return
     */
    private String upperCase(String str) {
        char[] ch = str.toCharArray();
        if (ch[0] >= 'a' && ch[0] <= 'z') {
            ch[0] = (char) (ch[0] - 32);
        }
        return new String(ch);
    }

    /**
     * 根据表名生成驼峰式名称
     * @param tableConfiguration
     * @return
     */
    private String buildName(TableConfiguration tableConfiguration) {
        StringBuilder stringBuilder = new StringBuilder();
        String tableName = tableConfiguration.getTableName();
        String[] strings = tableName.toLowerCase().split("_");
        for(String s : strings) {
            if(s != null && !"".equals(s)){
                stringBuilder.append(upperCase(s));
            }
        }
        return stringBuilder.toString();
    }


}
