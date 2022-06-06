package org.dtj.builder.java;

import lombok.Getter;
import lombok.Setter;
import org.dtj.data.TypeMap;
import org.dtj.exception.BuildException;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * @author godBless 2022/05/23
 * 成员变量
 */
@Getter
@Setter
public class JavaField {
    
    private List<String> annotationList;
    /**
     * 修饰符
     */
    private Modifier modifier;
    /**
     * 类型名字
     */
    private String typeName;
    /**
     * 类型引用
     */
    private String typeReference;
    /**
     * 该类型是否需要import
     */
    private boolean needImport;
    /**
     * 名字
     */
    private String name;
    /**
     * doc注释
     */
    private String doc;
    
    public JavaField(Modifier modifier, String typeName, String typeReference, String name) {
        this(modifier, typeName, typeReference, name, "");
    }
    
    public JavaField(Modifier modifier, String typeName, String typeReference, String name, String doc) {
        this(modifier, typeName, typeReference, false, name, doc);
    }
    
    public JavaField(Modifier modifier, String typeName, String typeReference, boolean needImport, String name, String doc) {
        this.annotationList = new LinkedList<>();
        this.modifier = modifier;
        this.typeName = typeName;
        this.typeReference = typeReference;
        this.needImport = needImport;
        this.name = name;
        this.doc = doc;
        validate();
    }
    
    public void validate() {
        if (modifier == null) {
            throw new NullPointerException("JavaField's modifier is null");
        }
        if (typeName == null || typeName.equals("")) {
            throw new NullPointerException("JavaField's typeName is null");
        }
        if (typeReference == null || typeReference.equals("")) {
            throw new NullPointerException("JavaField's typeReference is null");
        }
        if (name == null || name.equals("")) {
            throw new BuildException("JavaField's name is empty");
        }
    }
    
    public void addAnnotation(String annotation) {
        annotationList.add(annotation);
    }
    
    String getCode() {
        return getCode("");
    }
    
    /**
     * 生成代码
     * @param linePrefix 行前缀
     */
    String getCode(String linePrefix) {
        StringBuilder code = new StringBuilder();
        // doc注释
        if (doc != null && doc.length() > 0) {
            code.append(linePrefix).append("/**\n");
            code.append(linePrefix).append(" * ").append(doc).append("\n");
            code.append(linePrefix).append(" */\n");
        }
        // 注解
        annotationList.forEach(annotation -> code.append(linePrefix).append(annotation).append("\n"));
        // 属性
        code.append(linePrefix).append(modifier.getName()).append(" ").append(typeName).append(" ").append(name).append(";");
        return code.toString();
    }
    
    public String getTypeReference() {
        return typeReference;
    }
    
    public boolean isNeedImport() {
        return needImport;
    }
    
    public String getTypeName() {
        return typeName;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof JavaField)) {
            return false;
        }
        JavaField that = (JavaField) o;
        return name.equals(that.name);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
    
    public static String transferName(String columnName) {
        if (columnName == null) {
            return null;
        }
        StringBuilder fieldName = new StringBuilder();
        char[] chars = columnName.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c == '`') {
                continue;
            } else if (c != '_') {
                fieldName.append(c);
            } else if (i < chars.length - 1) {
                // _在不在末尾时,则删掉_并且_后一位变大写
                i++;
                fieldName.append(Character.toUpperCase(chars[i]));
            }
        }
        return fieldName.toString();
    }
    
    public static TypeMap transferType(String columnType) {
        return TypeMap.getTypeMap(columnType);
    }
}
