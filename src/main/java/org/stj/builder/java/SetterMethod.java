package org.stj.builder.java;

import java.util.Collections;

/**
 * @author godBless 2022/06/01
 */
public class SetterMethod extends JavaMethod {
    
    private String fieldTypeReference;
    
    public SetterMethod(String fieldTypeReference, String fieldType, String fieldName) {
        super(false, Modifier.PUBLIC, "void", "set" + fieldName.replaceFirst(String.valueOf(fieldName.charAt(0)), String.valueOf(Character.toUpperCase(fieldName.charAt(0)))));
        addParam(fieldType, fieldName);
        this.body = Collections.singletonList( "this." + fieldName + " = " + fieldName + ";");
        this.fieldTypeReference = fieldTypeReference;
    }
    
    public String getFieldTypeReference() {
        return fieldTypeReference;
    }
}
