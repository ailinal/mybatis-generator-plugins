package com.ailinal.plugin.mybatis.generator.types;

import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.internal.types.JavaTypeResolverDefaultImpl;

import java.sql.Types;
import java.time.LocalDateTime;

/**
 * @author yang
 */
public class CustomJavaTypeReslover extends JavaTypeResolverDefaultImpl {
    public CustomJavaTypeReslover() {
        super();
        typeMap.put(Types.TIMESTAMP, new JdbcTypeInformation("DATE", //$NON-NLS-1$
                new FullyQualifiedJavaType(LocalDateTime.class.getName())));
    }
}
