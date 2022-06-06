package org.dtj.data;

import lombok.Getter;

import java.util.Locale;

/**
 * 数据库-java类型映射
 */
@Getter
public enum TypeMap {
    INT("Integer", "java.lang.Integer", false),
    INTEGER("Integer", "java.lang.Integer", false),
    SMALLINT("Integer", "java.lang.Integer", false),
    TINYINT("Integer", "java.lang.Integer", false),
    
    BIGINT("Long", "java.lang.Long", false),
    
    BIT("Short", "java.lang.Short", false),
    
    CHAR("String", "java.lang.Short", false),
    TEXT("String", "java.lang.Short", false),
    VARCHAR("String", "java.lang.Short", false),
    
    BINARY("byte[]", "byte[]", false),
    BLOB("byte[]", "byte[]", false),
    VARBINARY("byte[]", "byte[]", false),
    
    DOUBLE("Double", "java.lang.Double", false),
    FLOAT("Double", "java.lang.Double", false),
    
    DECIMAL("BigDecimal", "java.math.BigDecimal", true),
    NUMERIC("BigDecimal", "java.math.BigDecimal", true),
    
    DATE("Date", "java.util.Date", true),
    DATETIME("Date", "java.util.Date", true),
    TIME("Date", "java.util.Date", true),
    TIMESTAMP("Date", "java.util.Date", true),
    
    OTHER("Object", "java.lang.Object", false);
    ;
    /**
     * java类型
     */
    private final String javaType;
    /**
     * java类型的全限定名
     */
    private final String javaReference;
    /**
     * 是否需要导入
     */
    private final boolean needImport;
    
    TypeMap(String javaType, String javaReference, boolean needImport) {
        this.javaType = javaType;
        this.javaReference = javaReference;
        this.needImport = needImport;
    }
    
    public static TypeMap getTypeMap(String type) {
        type = type.toUpperCase(Locale.ENGLISH);
        for (TypeMap value : values()) {
            if (value.name().equals(type)) {
                return value;
            }
        }
        return OTHER;
    }
}
