package org.stj.config;

import lombok.Getter;
import lombok.Setter;

/**
 * @author godBless 2022/05/23
 */
@Getter
@Setter
public class BuildConfig {
    
    /**
     * 源sql
     */
    private String sql;
    /**
     * 是否使用lombok
     */
    private boolean useLombok = false;
    /**
     * 源码输出路径前缀
     */
    private String baseClassPath;
    /**
     * 业务名称
     * 用于自动构建类名
     */
    private String businessName;
    /**
     * DO源码的输出路径
     */
    private String doBaseClassPath;
    /**
     * DO的包名
     */
    private String doPackageName;
    /**
     * DO的类名
     */
    private String doClassName;
    
    /**
     * DTO源码的输出路径
     */
    private String dtoBaseClassPath;
    /**
     * DTO的包名
     */
    private String dtoPackageName;
    /**
     * DTO的类名
     */
    private String dtoClassName;
    
    /**
     * mapper源码的输出路径
     */
    private String mapperClassBaseClassPath;
    /**
     * mapper的包名
     */
    private String mapperClassPackageName;
    /**
     * mapper的类名
     */
    private String mapperClassName;
    
    /**
     * mapper源码的输出路径
     */
    private String mapperXmlBaseClassPath;
    
    /**
     * service接口源码的输出路径
     */
    private String serviceInterfaceBaseClassPath;
    
    /**
     * service接口的包名
     */
    private String serviceInterfacePackageName;
    
    /**
     * service接口类型
     */
    private String serviceInterfaceClassName;
    
    /**
     * service实现类源码的输出路径
     */
    private String serviceImplBaseClassPath;
    
    /**
     * service实现类的包名
     */
    private String serviceImplPackageName;
    
    /**
     * service实现类
     */
    private String serviceImplClassName;
    
    /**
     * controller实现类源码的输出路径
     */
    private String controllerBaseClassPath;
    
    /**
     * controller实现类的包名
     */
    private String controllerPackageName;
    
    /**
     * controller实现类
     */
    private String controllerClassName;
    
    public String getDoBaseClassPath() {
        if (doBaseClassPath == null) {
            return baseClassPath;
        }
        return doBaseClassPath;
    }
    
    public String getDtoBaseClassPath() {
        if (dtoBaseClassPath == null) {
            return baseClassPath;
        }
        return dtoBaseClassPath;
    }
    
    public String getMapperClassBaseClassPath() {
        if (mapperClassBaseClassPath == null) {
            return baseClassPath;
        }
        return mapperClassBaseClassPath;
    }
    
    public String getMapperXmlBaseClassPath() {
        if (mapperXmlBaseClassPath == null) {
            return baseClassPath;
        }
        return mapperXmlBaseClassPath;
    }
    
    public String getServiceInterfaceBaseClassPath() {
        if (serviceInterfaceBaseClassPath == null) {
            return baseClassPath;
        }
        return serviceInterfaceBaseClassPath;
    }
    
    public String getServiceImplBaseClassPath() {
        if (serviceImplBaseClassPath == null) {
            return baseClassPath;
        }
        return serviceImplBaseClassPath;
    }
    
    public String getControllerBaseClassPath() {
        if (controllerBaseClassPath == null) {
            return baseClassPath;
        }
        return controllerBaseClassPath;
    }
    
    public String getDoClassName() {
        if (doClassName == null) {
            return businessName + "DO";
        }
        return doClassName;
    }
    
    public String getDtoClassName() {
        if (dtoClassName == null) {
            return businessName + "DTO";
        }
        return dtoClassName;
    }
    
    public String getMapperClassName() {
        if (mapperClassName == null) {
            return businessName + "Mapper";
        }
        return mapperClassName;
    }
    
    public String getServiceInterfaceClassName() {
        if (serviceInterfaceClassName == null) {
            return businessName + "Service";
        }
        return serviceInterfaceClassName;
    }
    
    public String getServiceImplClassName() {
        if (serviceImplClassName == null) {
            return getServiceInterfaceClassName() + "Impl";
        }
        return serviceImplClassName;
    }
    
    public String getControllerClassName() {
        if (controllerClassName == null) {
            return businessName + "Controller";
        }
        return controllerClassName;
    }
}
