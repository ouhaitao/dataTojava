package org.stj.util;

/**
 * @author godBless 2022/05/23
 */
public class LogUtil {
    
    public static void info(String log) {
        System.out.println(log);
    }
 
    public static void error(String log) {
        error(log, null);
    }
    
    public static void error(Throwable throwable) {
        error(null, throwable);
    }
    
    public static void error(String log, Throwable throwable) {
        if (log != null) {
            System.out.println(log);
        }
        if (throwable != null) {
            throwable.printStackTrace();
        }
    }
}
