package org.stj.builder.java;

import java.util.*;

/**
 * @author godBless 2022/05/23
 * java class生成器
 */
public class JavaObjectBuilder {
    
    /**
     * 是否使用lombok
     */
    private boolean useLombok;
    /**
     * 类的类型
     */
    private ClassType classType;
    /**
     * 包名
     */
    private String packageName;
    /**
     * 导入列表
     */
    private Set<String> importList;
    /**
     * 注释列表
     */
    private List<String> annotationList;
    /**
     * implements列表
     */
    private List<String> implementsList;
    /**
     * 类名
     */
    private String className;
    /**
     * 类型列表
     */
    private List<JavaField> fieldList;
    /**
     * 方法列表
     */
    private List<JavaMethod> methodList;
    /**
     * getter方法
     */
    private Map<String, GetterMethod> getterMethodMap;
    /**
     * setter方法
     */
    private Map<String, SetterMethod> setterMethodMap;
    
    public JavaObjectBuilder(String packageName, String className) {
        this(packageName, ClassType.CLASS, className, false);
    }
    
    public JavaObjectBuilder(String packageName, ClassType classType, String className) {
        this(packageName, classType, className, false);
    }
    
    public JavaObjectBuilder(String packageName, String className, boolean useLombok) {
        this(packageName, ClassType.CLASS, className, useLombok);
    }
    
    public JavaObjectBuilder(String packageName, ClassType classType, String className, boolean useLombok) {
        this.packageName = packageName;
        this.className = className;
        this.classType = classType;
        this.useLombok = useLombok;
        this.fieldList = new LinkedList<>();
        this.importList = new HashSet<>();
        this.annotationList = new LinkedList<>();
        this.methodList = new LinkedList<>();
        this.implementsList = new LinkedList<>();
        this.getterMethodMap = new HashMap<>();
        this.setterMethodMap = new HashMap<>();
        if (useLombok) {
            annotationList.add("@Data");
            importList.add("lombok.Data");
        }
    }
    
    public void addField(JavaField field) {
        addField(field, !useLombok, !useLombok);
    }
    
    /**
     * 添加属性
     * @param field 属性
     * @param generateGetter 是否生成getter
     * @param generateSetter 是否生成setter
     */
    public void addField(JavaField field, boolean generateGetter, boolean generateSetter) {
        fieldList.add(field);
        String typeReference = field.getTypeReference();
        if (field.isNeedImport()) {
            importList.add(typeReference);
        }
        String name = field.getName();
        String javaType = field.getTypeName();
        // 添加getter方法
        if (useLombok || generateGetter) {
            GetterMethod getter = new GetterMethod(typeReference, javaType, name);
            getterMethodMap.put(name, getter);
            if (generateGetter) {
                addMethod(getter);
            }
        }
        // 添加setter方法
        if (useLombok || generateSetter) {
            SetterMethod setter = new SetterMethod(typeReference, javaType, name);
            setterMethodMap.put(name, setter);
            if (generateSetter) {
                addMethod(setter);
            }
        }
    }
    
    public void addMethod(JavaMethod method) {
        this.methodList.add(method);
    }
    
    public void addImport(String reference) {
        this.importList.add(reference);
    }
    
    public void addImplements(String className) {
        this.implementsList.add(className);
    }
    
    public void addAnnotation(String annotation) {
        this.annotationList.add(annotation);
    }
    
    public String getClassName() {
        return className;
    }
    
    public String getReference() {
        return packageName + "." + className;
    }
    
    public List<JavaField> getFieldList() {
        return new ArrayList<>(fieldList);
    }
    
    public List<JavaMethod> getMethodList() {
        return new ArrayList<>(methodList);
    }
    
    public GetterMethod getGetterMethod(String fieldName) {
        return getterMethodMap.get(fieldName);
    }
    
    public SetterMethod getSetterMethod(String fieldName) {
        return setterMethodMap.get(fieldName);
    }
    
    public String getCode() {
        StringBuilder code = new StringBuilder();
        // 包名
        code.append("package ").append(packageName).append(";\n\n");
        
        // import
        if (importList.size() > 0) {
            importList.forEach(importPackage -> code.append("import ").append(importPackage).append(";\n"));
            code.append("\n");
        }
        
        // 注解
        annotationList.forEach(annotation -> code.append(annotation).append("\n"));
        
        // 类名
        code.append("public ").append(classType.getType()).append(" ").append(className);
        
        // implements
        if (implementsList.size() > 0) {
            code.append(" implements");
            implementsList.forEach(implement -> code.append(" ").append(implement).append(","));
            code.deleteCharAt(code.length() - 1);
        }
        code.append(" {\n\n");
    
        // 成员变量
        if (fieldList.size() > 0) {
            fieldList.forEach(field -> code.append(field.getCode("\t")).append("\n"));
            code.append("\n");
        }
        
        // 成员方法
        methodList.forEach(method -> code.append(method.getCode("\t")).append("\n\n"));
        code.append("}");
        return code.toString();
    }
    
}
