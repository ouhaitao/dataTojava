package org.dtj.exception;

/**
 * @author godBless 2022/05/23
 * 构建期间抛出的异常
 */
public class BuildException extends RuntimeException {
    
    public BuildException(String message) {
        super(message);
    }
}
