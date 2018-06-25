package com.ailinal.plugin.mybatis.generator.types;

import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.internal.types.JavaTypeResolverDefaultImpl;

import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * @author yang
 */
public class CustomJavaTypeResolver extends JavaTypeResolverDefaultImpl {
    public CustomJavaTypeResolver() {
        super();
        typeMap.put(Types.TIMESTAMP, new JdbcTypeInformation("TIMESTAMP", //$NON-NLS-1$
                new FullyQualifiedJavaType(LocalDateTime.class.getName())));
        typeMap.put(Types.DATE, new JdbcTypeInformation("DATE", //$NON-NLS-1$
                new FullyQualifiedJavaType(LocalDate.class.getName())));
        typeMap.put(Types.TIME, new JdbcTypeInformation("TIME", //$NON-NLS-1$
                new FullyQualifiedJavaType(LocalTime.class.getName())));
    }
}
