package com.littmem.plugin.mybatis.generator.plugins;

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

import java.io.File;
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

    private String mapperExtName;

    private String mapperName;

    private String xmlMapperName;

    private String xmlMapperExtName;

    private String extSuffix;

    private String mapperExtPackageName;

    private String xmlMapperExtPackageName;


    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public void initialized(IntrospectedTable introspectedTable) {

        if (extSuffix == null || "".equals(extSuffix)) {
            extSuffix = "Ext";
        }
        if (mapperExtPackageName == null || "".equals(mapperExtPackageName)){
            mapperExtPackageName = context.getJavaClientGeneratorConfiguration().getTargetPackage()+".ext";
        }
        if (xmlMapperExtPackageName == null || "".equals(xmlMapperExtPackageName)){
            xmlMapperExtPackageName = context.getSqlMapGeneratorConfiguration().getTargetPackage()+".ext";
        }

        mapperName = introspectedTable.getMyBatis3JavaMapperType();
        mapperExtName = mapperName.replace("Mapper", extSuffix + "Mapper");

        String[] stringArray= mapperExtName.split("\\.");
        mapperExtName = mapperExtPackageName+"."+ stringArray[stringArray.length - 1];

        xmlMapperName = introspectedTable.getMyBatis3XmlMapperFileName();
        xmlMapperExtName = xmlMapperName.replace("Mapper", extSuffix + "Mapper");

    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles() {
        List<GeneratedJavaFile> files = new ArrayList<GeneratedJavaFile>();
        if (isExtJavaFileExits()) {
            System.out.println( mapperExtName + ".java exits do nothing.");
            return files;
        }
        System.out.println("generate " + mapperExtName + ".java");
        files.add(generatedExtJavaFile());
        return files;
    }

    @Override
    public List<GeneratedXmlFile> contextGenerateAdditionalXmlFiles() {
        List<GeneratedXmlFile> files = new ArrayList<GeneratedXmlFile>();
        if (isExtXmlFileExits()) {
            System.out.println( xmlMapperExtName + " exits do nothing.");
            return files;
        }
        System.out.println("generate " + xmlMapperExtName );
        files.add(generatedExtXmlFile());
        return files;
    }

    /**
     * 生成Ext接口
     *
     * @return Mapper.java
     */
    private GeneratedJavaFile generatedExtJavaFile() {

        // 拼装接口
        Interface inter = new Interface(mapperExtName);
        inter.addSuperInterface(new FullyQualifiedJavaType(mapperName));
        inter.setVisibility(JavaVisibility.PUBLIC);

        // 添加注解 与 注解引入
        String anno = "@Mapper";
        String annoClass = "org.apache.ibatis.annotations.Mapper";

        System.out.println("anno = " + anno);
        System.out.println("annoClass = " + annoClass);


        inter.addAnnotation(anno);

        inter.addImportedType(new FullyQualifiedJavaType(annoClass));

        return new GeneratedJavaFile(inter, context.getJavaClientGeneratorConfiguration().getTargetProject(), new DefaultJavaFormatter());
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
        root.addAttribute(new Attribute("namespace", mapperExtName));
        root.addElement(new TextElement(""));
        document.setRootElement(root);

        String targetProject = context.getSqlMapGeneratorConfiguration().getTargetProject();

        // 生成ext xml文件
        return new GeneratedXmlFile(document, xmlMapperExtName, xmlMapperExtPackageName, targetProject, false, context.getXmlFormatter());
    }


    private boolean isExtJavaFileExits() {
        return new File(context.getJavaClientGeneratorConfiguration().getTargetProject() +"/"+ mapperExtName.replace('.','/')+ ".java").exists();
    }

    private boolean isExtXmlFileExits() {
        String fileName = context.getSqlMapGeneratorConfiguration().getTargetProject() + "/" + xmlMapperExtPackageName.replace('.','/')+"/"+ xmlMapperExtName;
        System.out.println(fileName);
        return new File(fileName).exists();
    }
}
