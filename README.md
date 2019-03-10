# mybatis-generator-plugins


## ExtPlugin
在Mapper接口类同级生成ext包,并生成对应的extMapper,继承mybatis generator生成Mapper,xmlMapper同理;达到平时开发修改Ext内容,防止再次使用mybatis generator生成文件被覆盖;    
生成的xml文件不再追加代码，而是覆盖代码

### 使用

- clone本项目
```
git clone git@github.com:ailinal/mybatis-generator-plugins.git
cd mybatis-generator-plugins
mvn install
```
- pom.xml
```xml
 <build>
        <plugins>
            <!--mybatis-generator插件-->
            <plugin>
                <groupId>org.mybatis.generator</groupId>
                <artifactId>mybatis-generator-maven-plugin</artifactId>
                <version>1.3.6</version>
                <configuration>
                    <verbose>true</verbose>
                    <overwrite>true</overwrite>
                </configuration>
                <dependencies>
                    <!--此处添加一个mysql-connector-java依赖可以防止找不到jdbc Driver-->
                    <dependency>
                        <groupId>mysql</groupId>
                        <artifactId>mysql-connector-java</artifactId>
                        <version>5.1.46</version>
                        <scope>runtime</scope>
                    </dependency>
                    <dependency>
                        <groupId>dev.metaspace.plugin</groupId>
                        <artifactId>mybatis-generator-plugins</artifactId>
                        <version>1.0.0.RELEASE</version>
                    </dependency>
                </dependencies>

            </plugin>

        </plugins>
    </build>
```
- generatorConfig.xml
```xml
<plugin type="ExtPlugin(全类名)">
    <!-- 生成额外Mapper接口名，（原Mapper接口名UsersMapper ==> Users + extSuffix + Mapper） -->
    <property name = "extSuffix" value = "XXX(自己取名，默认为Ext)">
    <!-- 生成额外Mapper文件存放包名，默认在原Mapper文件所在包下创建ext -->
    <property name = "mapperExtPackageName" value = "XXX(自己取名,生成额外的mapper文件所存的包)"> 
    <!-- 生成额外Mapper.xml文件存放包名，默认在原Mapper.xml文件所在包下创建ext -->
    <property name = "xmlMapperExtPackageName" value = "XXX(自己取名,生成额外的Mapper.xml文件所存的包)"> 
</plugin>
```

## LombokPlugin
使生成的model添加lombok @Data注解;禁止setter getter方法生成; 
