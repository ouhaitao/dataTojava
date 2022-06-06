package org.stj.builder.java;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author godBless 2022/06/01
 */
@AllArgsConstructor
@Getter
public enum ClassType {
    
    CLASS("class"),
    INTERFACE("interface"),
    ABSTRACT_CLASS("abstract class");
    
    private String type;
}
