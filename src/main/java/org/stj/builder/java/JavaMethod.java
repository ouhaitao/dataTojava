package org.stj.builder.java;

import org.stj.exception.BuildException;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author godBless 2022/05/24
 * java方法
 */
public class JavaMethod {
    
    protected boolean isAbstract;
    /**
     * 修饰符
     */
    protected Modifier modifier;
    /**
     * 返回类型
     */
    protected String returnType;
    /**
     * 名字
     */
    protected String name;
    /**
     * 注解列表
     */
    protected List<String> annotationList;
    /**
     * 参数列表
     * eg. "Integer param"
     * eg. "@RequestBody Integer param"
     */
    protected List<String> paramList;
    /**
     * 方法体
     * 一个元素就是一行代码
     */
    protected List<String> body;
    
    public JavaMethod(boolean isAbstract, Modifier modifier, String returnType, String name) {
        this(isAbstract, modifier, returnType, name, Collections.emptyList());
    }
    
    public JavaMethod(boolean isAbstract, Modifier modifier, String returnType, String name, List<String> body) {
        this.isAbstract = isAbstract;
        this.modifier = modifier;
        this.returnType = returnType;
        this.name = name;
        this.paramList = new LinkedList<>();
        this.body = body;
        this.annotationList = new LinkedList<>();
        validate();
    }
    
    public void validate() {
        if (returnType == null || returnType.equals("")) {
            throw new BuildException("JavaMethod's returnType is empty");
        }
        if (name == null || name.equals("")) {
            throw new BuildException("JavaMethod's name is empty");
        }
    }
    
    public void addParam(String type, String name) {
        addParam(null, type, name);
    }
    
    public void addParam(String annotation, String type, String name) {
        if (type == null || type.equals("")) {
            throw new BuildException("param's type is empty");
        }
        if (name == null || name.equals("")) {
            throw new BuildException("param's type is empty");
        }
        String param = type + " " + name;
        if (annotation != null && !annotation.equals("")) {
            param = annotation + " " + param;
        }
        paramList.add(param);
    }
    
    public void addAnnotation(String annotation) {
        this.annotationList.add(annotation);
    }
    
    public String getName() {
        return name;
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
    
        // 注解
        annotationList.forEach(annotation -> code.append(linePrefix).append(annotation).append("\n"));
        
        // 构造方法签名
        code.append(linePrefix).append(modifier.getName()).append(modifier.getName().length() == 0 ? "" : " ").append(returnType).append(" ").append(name);
        code.append("(");
        if (paramList.size() > 0) {
            paramList.forEach(param -> code.append(param).append(", "));
            code.delete(code.length() - 2, code.length());
        }
        code.append(")");
        if (isAbstract) {
            return code.append(";").toString();
        }
        // 构造方法体
        code.append(" {\n");
        body.forEach(b -> code.append(linePrefix).append("\t").append(b).append("\n"));
        code.append(linePrefix).append("}");
        return code.toString();
    }
}
