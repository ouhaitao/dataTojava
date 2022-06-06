package org.dtj.exception;

/**
 * @author godBless 2022/05/23
 * 文件操作抛出的异常
 */
public class FileException extends RuntimeException {
    
    public FileException() {
        super();
    }
    
    public FileException(String message) {
        super(message);
    }
}
