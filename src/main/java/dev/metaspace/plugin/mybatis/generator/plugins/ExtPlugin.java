package dev.metaspace.plugin.mybatis.generator.plugins;

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
import org.mybatis.generator.logging.Log;
import org.mybatis.generator.logging.LogFactory;

import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description: 生成额外的Mapper.java和Mapper.xml
 *
 * @author yang
 * @version 18-6-22
 */
public class ExtPlugin extends PluginAdapter {

    private final Log log = LogFactory.getLog(ExtPlugin.class);

    /**
     * 额外后缀，如（UsersMapper => Users + extSuffix + Mapper）
     */
    private String extSuffix;

    /**
     * 额外的mapper包名称
     */
    private String mapperExtPackageName;

    /**
     * 额外的mapper对应的xml文件包名称
     */
    private String xmlMapperExtPackageName;

    /**
     * 额外的mapper文件的项目磁盘路径
     */
    private String mapperTargetProject;

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
        //从配置中读额外后缀
        extSuffix = properties.getProperty("extSuffix");
        //从配置中读额外mapper生成的包名
        mapperExtPackageName = properties.getProperty("mapperExtPackageName");
        //从配置中读额外mapper.xml生成的包名
        xmlMapperExtPackageName = properties.getProperty("xmlMapperExtPackageName");

        if (this.extSuffix == null || "".equals(this.extSuffix)) {
            this.extSuffix = "Ext";
        }
        if (mapperExtPackageName == null || "".equals(mapperExtPackageName)) {
            mapperExtPackageName = context.getJavaClientGeneratorConfiguration().getTargetPackage() + ".ext";
        }
        if (xmlMapperExtPackageName == null || "".equals(xmlMapperExtPackageName)) {
            xmlMapperExtPackageName = context.getSqlMapGeneratorConfiguration().getTargetPackage() + ".ext";
        }

        mapperTargetProject = context.getJavaClientGeneratorConfiguration().getTargetProject();
        xmlMapperTargetProject = context.getSqlMapGeneratorConfiguration().getTargetProject();
    }


    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {

        //原mapper全限定java类名
        String originFullQualifiedJavaTypeName = introspectedTable.getMyBatis3JavaMapperType();
        int lastIndex = originFullQualifiedJavaTypeName.lastIndexOf('.');
        //新的mapper非限定类名
        String newNonQualifiedJavaTypeName = originFullQualifiedJavaTypeName.substring(lastIndex + 1).replace("Mapper", extSuffix + "Mapper");

        //已存在ext不生成
        if (isExtJavaFileExits(newNonQualifiedJavaTypeName)) {

            log.warn(newNonQualifiedJavaTypeName.concat(".java already exists. do nothing."));
            return Collections.emptyList();
        }

        return Collections.singletonList(generatedExtJavaFile(originFullQualifiedJavaTypeName, newNonQualifiedJavaTypeName));
    }


    /**
     * 生成extMapper
     *
     * @param originFullQualifiedJavaTypeName 原始Mapper的全限定类名
     * @param newNonQualifiedJavaTypeName     extMapper的非限定类名
     * @return 生成的extMapper
     */
    private GeneratedJavaFile generatedExtJavaFile(String originFullQualifiedJavaTypeName, String newNonQualifiedJavaTypeName) {


        // 拼装接口
        Interface inter = new Interface(mapperExtPackageName + "." + newNonQualifiedJavaTypeName);
        inter.addSuperInterface(new FullyQualifiedJavaType(originFullQualifiedJavaTypeName));
        inter.setVisibility(JavaVisibility.PUBLIC);

        // 添加注解 与 注解引入
        inter.addImportedType(
                new FullyQualifiedJavaType("org.apache.ibatis.annotations.Mapper"));
        inter.addAnnotation("@Mapper");

        return new GeneratedJavaFile(inter, mapperTargetProject, new DefaultJavaFormatter());
    }

    private boolean isExtJavaFileExits(String nonQualifiedJavaTypeName) {
        return new File(mapperTargetProject + "/" + mapperExtPackageName.replace(".", "/") + "/" + nonQualifiedJavaTypeName + ".java").exists();
    }


    @Override
    public List<GeneratedXmlFile> contextGenerateAdditionalXmlFiles(IntrospectedTable introspectedTable) {

        String originFullQualifiedJavaTypeName = introspectedTable.getMyBatis3JavaMapperType();
        int lastIndex = originFullQualifiedJavaTypeName.lastIndexOf('.');
        //新的mapper非限定类名
        String newNonQualifiedJavaTypeName = originFullQualifiedJavaTypeName.substring(lastIndex + 1).replace("Mapper", extSuffix + "Mapper");

        String extMapperFullQualifiedJavaTypeName = mapperExtPackageName
                .concat(".")
                .concat(newNonQualifiedJavaTypeName);

        String extXmlFileName = introspectedTable.getMyBatis3XmlMapperFileName().replace("Mapper", extSuffix + "Mapper");

        //已存在ext不生成
        if (isExtXmlFileExits(extXmlFileName)) {
            log.warn(extXmlFileName.concat(" already exists. do nothing."));
            return Collections.emptyList();
        }

        return Collections.singletonList(generatedExtXmlFile(extMapperFullQualifiedJavaTypeName, extXmlFileName));
    }

    /**
     * @param extMapperFullQualifiedJavaTypeName extMapper的全限定类名,用作xml的namespace
     * @param xmlFileName                        extXml的名称
     * @return 生成的extXml
     */
    private GeneratedXmlFile generatedExtXmlFile(String extMapperFullQualifiedJavaTypeName, String xmlFileName) {

        // 创建文档对象
        Document document = new Document(XmlConstants.MYBATIS3_MAPPER_PUBLIC_ID, XmlConstants.MYBATIS3_MAPPER_SYSTEM_ID);
        // 创建根节点
        XmlElement root = new XmlElement("mapper");
        root.addAttribute(new Attribute("namespace", extMapperFullQualifiedJavaTypeName));
        root.addElement(new TextElement(""));
        document.setRootElement(root);

        // 生成ext xml文件
        return new GeneratedXmlFile(document, xmlFileName, xmlMapperExtPackageName, xmlMapperTargetProject, false, context.getXmlFormatter());
    }


    private boolean isExtXmlFileExits(String xmlFileName) {
        return new File(xmlMapperTargetProject + "/" + xmlMapperExtPackageName.replace('.', '/') + "/" + xmlFileName).exists();
    }

}
