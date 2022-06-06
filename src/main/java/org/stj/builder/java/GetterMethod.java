package org.stj.builder.java;

import java.util.Collections;

/**
 * @author godBless 2022/06/01
 * getter方法
 */
public class GetterMethod extends JavaMethod {
    
    private String returnTypeReference;
    
    public GetterMethod(String fieldTypeReference, String fieldType, String fieldName) {
        super(false, Modifier.PUBLIC, fieldType, "get" + fieldName.replaceFirst(String.valueOf(fieldName.charAt(0)), String.valueOf(Character.toUpperCase(fieldName.charAt(0)))));
        this.body = Collections.singletonList( "return this." + fieldName + ";");
        this.returnTypeReference = fieldTypeReference;
    }
    
    public String getReturnTypeReference() {
        return returnTypeReference;
    }
}
