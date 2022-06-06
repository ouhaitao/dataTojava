package org.dtj.builder.java;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author godBless 2022/06/01
 */
@AllArgsConstructor
@Getter
public enum Modifier {
    
    PUBLIC("public"),
    PRIVATE("private"),
    PROTECTED("protected"),
    DEFAULT("");
    
    private String name;
}
