package com.ailinal.plugin.mybatis.generator.types;

import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.internal.types.JavaTypeResolverDefaultImpl;

import java.sql.Types;
import java.time.Instant;

/**
 * @author yang
 */
public class CustomJavaTypeResolver extends JavaTypeResolverDefaultImpl {
    public CustomJavaTypeResolver() {
        super();
        typeMap.put(Types.TIMESTAMP, new JdbcTypeInformation("TIMESTAMP", //$NON-NLS-1$
                new FullyQualifiedJavaType(Instant.class.getName())));
        typeMap.put(Types.DATE, new JdbcTypeInformation("DATE", //$NON-NLS-1$
                new FullyQualifiedJavaType(Instant.class.getName())));
        typeMap.put(Types.TIME, new JdbcTypeInformation("TIME", //$NON-NLS-1$
                new FullyQualifiedJavaType(Instant.class.getName())));
    }
}
