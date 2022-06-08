import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.create.table.Index;
import org.stj.builder.java.*;
import org.stj.builder.xml.TextNode;
import org.stj.builder.xml.XmlBuilder;
import org.stj.builder.xml.XmlNode;
import org.stj.config.BuildConfig;
import org.stj.data.TypeMap;
import org.stj.exception.BuildException;
import org.stj.util.FileUtil;
import org.stj.util.LogUtil;

import java.util.*;

/**
 * @author godBless 2022/05/23
 * 通过建表SQL,自动生成：
 * 1. DO
 * 2. DTO
 * 3. mapper
 * 4. 基础的增删改查功能涉及到的RO、controller、service、mapper
 */
public final class SqlToJava {
    
    private SqlToJava() {}
    
    public static void parse(BuildConfig config) {
        new SqlToJava().build(config);
    }
    
    public void build(BuildConfig config) {
        validate(config);
        
        String sql = config.getSql();
        Statement statement = null;
        try {
            statement = CCJSqlParserUtil.parse(sql);
        } catch (JSQLParserException e) {
            LogUtil.error("解析SQL错误", e);
        }
        if (!(statement instanceof CreateTable)) {
            return;
        }
        CreateTable table = (CreateTable) statement;
        List<ColumnDefinition> columnDefinitions = table.getColumnDefinitions();
        if (columnDefinitions == null || columnDefinitions.size() <= 0) {
            return;
        }
        
        // 生成DO
        JavaObjectBuilder doBuilder = buildObjectByData(config.getDoPackageName(), config.getDoClassName(), config.isUseLombok(), columnDefinitions);
        saveFile(config.getDoBaseClassPath(), config.getDoPackageName(), doBuilder.getClassName() + ".java", doBuilder.getCode());
        // 生成DTO
        JavaObjectBuilder dtoBuilder = buildObjectByData(config.getDtoPackageName(), config.getDtoClassName(), config.isUseLombok(), columnDefinitions);
        saveFile(config.getDtoBaseClassPath(), config.getDtoPackageName(), dtoBuilder.getClassName() + ".java", dtoBuilder.getCode());
        // 生成mapper类
        JavaObjectBuilder mapperClassBuilder = buildMapperClass(config.getMapperClassPackageName(), config.getMapperClassName(), doBuilder.getClassName(), doBuilder.getReference());
        saveFile(config.getMapperClassBaseClassPath(), config.getMapperClassPackageName(), mapperClassBuilder.getClassName() + ".java", mapperClassBuilder.getCode());
        // 生成mapper.xml
        XmlBuilder mapperXmlBuilder = buildMapperXml(mapperClassBuilder.getReference(), doBuilder.getClassName(), doBuilder.getReference(), table);
        saveFile(config.getMapperXmlBaseClassPath(), null, config.getMapperClassName() + ".xml", mapperXmlBuilder.getCode());
        // 生成service接口
        JavaObjectBuilder serviceInterfaceBuilder = buildServiceInterface(config.getServiceInterfacePackageName(), config.getServiceInterfaceClassName(), dtoBuilder.getClassName(), dtoBuilder.getReference());
        saveFile(config.getServiceInterfaceBaseClassPath(), config.getServiceInterfacePackageName(), serviceInterfaceBuilder.getClassName() + ".java", serviceInterfaceBuilder.getCode());
        // 生成service实现类
        JavaObjectBuilder serviceImplBuilder = buildServiceImpl(config.getServiceImplPackageName(), config.getServiceImplClassName(), serviceInterfaceBuilder, dtoBuilder, doBuilder, mapperClassBuilder);
        saveFile(config.getServiceImplBaseClassPath(), config.getServiceImplPackageName(), config.getServiceImplClassName() + ".java", serviceImplBuilder.getCode());
        // 生成controller
        JavaObjectBuilder controllerBuilder = buildController(config, serviceInterfaceBuilder, dtoBuilder);
        saveFile(config.getControllerBaseClassPath(), config.getControllerPackageName(), config.getControllerClassName() + ".java", controllerBuilder.getCode());
    }
    
    private void validate(BuildConfig config) {
        if (config.getSql() == null || config.getSql().equals("")) {
            throw new BuildException("源SQL为空");
        }
        if (config.getDtoBaseClassPath() == null || config.getDtoBaseClassPath().equals("")) {
            throw new BuildException("DTO的源码输出路径为空");
        }
        if (config.getDoBaseClassPath() == null || config.getDoBaseClassPath().equals("")) {
            throw new BuildException("DO的源码输出路径为空");
        }
        if (config.getControllerBaseClassPath() == null || config.getControllerBaseClassPath().equals("")) {
            throw new BuildException("Controller的源码输出路径为空");
        }
        if (config.getServiceInterfaceBaseClassPath() == null || config.getServiceInterfaceBaseClassPath().equals("")) {
            throw new BuildException("Service接口的源码输出路径为空");
        }
        if (config.getServiceImplBaseClassPath() == null || config.getServiceImplBaseClassPath().equals("")) {
            throw new BuildException("Service实现类的源码输出路径为空");
        }
        if (config.getMapperClassBaseClassPath() == null || config.getMapperClassBaseClassPath().equals("")) {
            throw new BuildException("Mapper接口的源码输出路径为空");
        }
        if (config.getMapperXmlBaseClassPath() == null || config.getMapperXmlBaseClassPath().equals("")) {
            throw new BuildException("MapperXml的源码输出路径为空");
        }
    
        if (config.getDtoClassName() == null || config.getDtoClassName().equals("")) {
            throw new BuildException("DTO的源码输出路径为空");
        }
        if (config.getDoClassName() == null || config.getDoClassName().equals("")) {
            throw new BuildException("DO的源码输出路径为空");
        }
        if (config.getControllerClassName() == null || config.getControllerClassName().equals("")) {
            throw new BuildException("Controller的源码输出路径为空");
        }
        if (config.getServiceInterfaceClassName() == null || config.getServiceInterfaceClassName().equals("")) {
            throw new BuildException("Service接口的源码输出路径为空");
        }
        if (config.getServiceImplClassName() == null || config.getServiceImplClassName().equals("")) {
            throw new BuildException("Service实现类的源码输出路径为空");
        }
        if (config.getMapperClassName() == null || config.getMapperClassName().equals("")) {
            throw new BuildException("Mapper接口的源码输出路径为空");
        }
    }
    
    /**
     * 通过数据库列来创建类
     * @param packageName 包名
     * @param className 类名
     * @param useLombok 是否使用lombok
     * @param columnDefinitionList 数据库列定义
     */
    private JavaObjectBuilder buildObjectByData(String packageName, String className, boolean useLombok, List<ColumnDefinition> columnDefinitionList) {
        // 生成DO代码
        JavaObjectBuilder builder = new JavaObjectBuilder(packageName, className, useLombok);
        for (ColumnDefinition columnDefinition : columnDefinitionList) {
            String filedName = getFieldName(columnDefinition.getColumnName());
            TypeMap filedType = getFieldType(columnDefinition.getColDataType().getDataType());
            String doc = getDoc(columnDefinition);
            JavaField field = new JavaField(Modifier.PRIVATE, filedType.getJavaType(), filedType.getJavaReference(), filedType.isNeedImport(), filedName, doc);
            builder.addField(field);
        }
        return builder;
    }
    
    /**
     * 创建mapper类
     * @param packageName 包名
     * @param className 类名
     * @param doClassName DO类名
     * @param doReference DO的引用
     */
    private JavaObjectBuilder buildMapperClass(String packageName, String className, String doClassName, String doReference) {
        String doClassParamName = doClassName.substring(0, 1).toLowerCase(Locale.ROOT) + doClassName.substring(1);
        JavaObjectBuilder builder = new JavaObjectBuilder(packageName, ClassType.INTERFACE, className);
        builder.addImport(doReference);
        // insert
        JavaMethod insert = new JavaMethod(true, Modifier.DEFAULT, "int", "insert");
        insert.addParam(doClassName, doClassParamName);
        builder.addMethod(insert);
        // update
        JavaMethod update = new JavaMethod(true, Modifier.DEFAULT, "int", "update");
        update.addParam(doClassName, doClassParamName);
        builder.addMethod(update);
        // delete
        JavaMethod delete = new JavaMethod(true, Modifier.DEFAULT, "int", "delete");
        delete.addParam(doClassName, doClassParamName);
        builder.addMethod(delete);
        // select
        JavaMethod select = new JavaMethod(true, Modifier.DEFAULT, doClassName, "select");
        select.addParam(doClassName, doClassParamName);
        builder.addMethod(select);
    
        return builder;
    }
    
    /**
     * 创建mapper xml
     * @param mapperClassReference mapper类的引用
     * @param resultMapName resultMap名
     * @param doReference DO引用
     * @param table 表信息
     */
    private XmlBuilder buildMapperXml(String mapperClassReference, String resultMapName, String doReference, CreateTable table) {
        XmlNode mapper = new XmlNode("mapper");
        mapper.addProperty("namespace", mapperClassReference);
        
        // 获取主键
        List<Index> indexList = table.getIndexes();
        Set<String> primaryKeyColumn;
        if (indexList != null && indexList.size() > 0) {
            primaryKeyColumn = new HashSet<>();
            indexList.stream()
                .filter(index -> index.getType().toUpperCase(Locale.ROOT).equals("PRIMARY KEY")).map(Index::getColumns)
                .forEach(columnParams -> columnParams.stream().map(Index.ColumnParams::getColumnName).forEach(primaryKeyColumn::add));
        } else {
            primaryKeyColumn = Collections.emptySet();
        }
        // 构建ResultMap
        XmlNode resultMap = new XmlNode("resultMap");
        resultMap.addProperty("id", resultMapName);
        resultMap.addProperty("type", doReference);
        List<ColumnDefinition> columnDefinitionList = table.getColumnDefinitions();
        for (ColumnDefinition columnDefinition : columnDefinitionList) {
            String columnName = columnDefinition.getColumnName().replace("`", "");
            String fieldName = getFieldName(columnName);
            XmlNode node;
            if (primaryKeyColumn.contains(columnName)) {
                node = new XmlNode("id");
            } else {
                node = new XmlNode("result");
            }
            node.addProperty("column", columnName);
            node.addProperty("property", fieldName);
            resultMap.addChild(node);
        }
        mapper.addChild(resultMap);
        // 构建增删改查sql
        String tableName = table.getTable().getName();
        // insert
        mapper.addChild(buildInsertSqlNode(table));
        // 有主键才会生成
        if (primaryKeyColumn.size() > 0) {
            // update
            mapper.addChild(buildUpdateSqlNode(primaryKeyColumn, columnDefinitionList, tableName));
            // delete
            mapper.addChild(buildDeleteSqlNode(primaryKeyColumn, tableName));
            // select
            mapper.addChild(buildSelectSqlNode(primaryKeyColumn, tableName, resultMapName));
        }
        XmlBuilder xmlBuilder = new XmlBuilder(mapper);
        xmlBuilder.addHeaderNode(new TextNode("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"));
        xmlBuilder.addHeaderNode(new TextNode("<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\" >"));
        return xmlBuilder;
    }
    
    /**
     * 生成insert语句
     */
    private XmlNode buildInsertSqlNode(CreateTable table) {
        StringBuilder column = new StringBuilder();
        StringBuilder field = new StringBuilder();
        for (ColumnDefinition columnDefinition : table.getColumnDefinitions()) {
            String columnName = columnDefinition.getColumnName();
            String fieldName = getFieldName(columnName);
            column.append(columnName).append(", ");
            field.append("#{").append(fieldName).append("}, ");
        }
        column.delete(column.length() - 2, column.length());
        field.delete(field.length() - 2, field.length());
        String sql = "insert into " + table.getTable().getName() + "(" + column + ") values(" + field + ")";
        XmlNode insert = new XmlNode("insert");
        insert.addProperty("id", "insert");
        TextNode insertSql = new TextNode(sql);
        insert.addChild(insertSql);
        return insert;
    }
    
    /**
     * 生成delete语句
     */
    private XmlNode buildDeleteSqlNode(Set<String> primaryKeyColumn, String tableName) {
        StringBuilder sql = new StringBuilder();
        sql.append("delete from ").append(tableName).append(" where ");
        primaryKeyColumn.forEach(primaryKey -> sql.append(primaryKey).append(" = ").append("#{").append(getFieldName(primaryKey)).append("}").append(" and "));
        sql.delete(sql.length() - 5, sql.length());
        XmlNode delete = new XmlNode("delete");
        delete.addProperty("id", "delete");
        TextNode deleteSql = new TextNode(sql.toString());
        delete.addChild(deleteSql);
        return delete;
    }
    
    /**
     * 生成select语句
     */
    private XmlNode buildSelectSqlNode(Set<String> primaryKeyColumn, String tableName, String resultMapName) {
        StringBuilder sql = new StringBuilder();
        sql.append("select * from ").append(tableName).append(" where ");
        primaryKeyColumn.forEach(primaryKey -> sql.append(primaryKey).append(" = ").append("#{").append(getFieldName(primaryKey)).append("}").append(" and "));
        sql.delete(sql.length() - 5, sql.length());
        XmlNode select = new XmlNode("select");
        select.addProperty("id", "select");
        select.addProperty("resultMap", resultMapName);
        TextNode selectSql = new TextNode(sql.toString());
        select.addChild(selectSql);
        return select;
    }
    
    /**
     * 生成update语句
     */
    private XmlNode buildUpdateSqlNode(Set<String> primaryKeyColumn, List<ColumnDefinition> columnDefinitionList, String tableName) {
        XmlNode update = new XmlNode("update");
        update.addProperty("id", "update");
        update.addChild(new TextNode("update " + tableName));
        
        XmlNode set = new XmlNode("set");
        for (ColumnDefinition columnDefinition : columnDefinitionList) {
            if (primaryKeyColumn.contains(columnDefinition.getColumnName())) {
                continue;
            }
            XmlNode anIf = new XmlNode("if");
            String columnName = columnDefinition.getColumnName();
            String fieldName = getFieldName(columnName);
            anIf.addProperty("test", fieldName + " != null");
            anIf.addChild(new TextNode(columnName + " = " + "#{" + fieldName + "},"));
            set.addChild(anIf);
        }
        update.addChild(set);
        
        StringBuilder sql = new StringBuilder();
        primaryKeyColumn.forEach(primaryKey -> sql.append(primaryKey).append(" = ").append("#{").append(getFieldName(primaryKey)).append("}").append(" and "));
        sql.delete(sql.length() - 5, sql.length());
        update.addChild(new TextNode("where " + sql));
        return update;
    }
    
    /**
     * 生成service接口
     * @param packageName 包名
     * @param className 类名
     * @param dtoClassName DTO类名
     * @param dtoReference DTO引用
     */
    private JavaObjectBuilder buildServiceInterface(String packageName, String className, String dtoClassName, String dtoReference) {
        JavaObjectBuilder builder = new JavaObjectBuilder(packageName, ClassType.INTERFACE, className);
        builder.addImport(dtoReference);
        // getDetail
        JavaMethod get = new JavaMethod(true, Modifier.DEFAULT, dtoClassName, "get");
        get.addParam(dtoClassName, "dto");
        builder.addMethod(get);
        // add
        JavaMethod add = new JavaMethod(true, Modifier.DEFAULT, "boolean", "add");
        add.addParam(dtoClassName, "dto");
        builder.addMethod(add);
        // delete
        JavaMethod delete = new JavaMethod(true, Modifier.DEFAULT, "boolean", "delete");
        delete.addParam(dtoClassName, "dto");
        builder.addMethod(delete);
        // update
        JavaMethod update = new JavaMethod(true, Modifier.DEFAULT, "boolean", "update");
        update.addParam(dtoClassName, "dto");
        builder.addMethod(update);
        
        return builder;
    }
    
    /**
     * 生成service实现类
     * @param packageName 包名
     * @param className 类名
     * @param interfaceBuilder 接口类信息
     * @param dtoBuilder  dto类信息
     * @param doBuilder do类信息
     * @param mapperClassBuilder mapper类信息
     */
    private JavaObjectBuilder buildServiceImpl(String packageName, String className, JavaObjectBuilder interfaceBuilder, JavaObjectBuilder dtoBuilder, JavaObjectBuilder doBuilder, JavaObjectBuilder mapperClassBuilder) {
    
        String doClassName = doBuilder.getClassName();
        String dtoClassName = dtoBuilder.getClassName();
        String mapperClassName = mapperClassBuilder.getClassName();
        String doClassParamName = doClassName.substring(0, 1).toLowerCase(Locale.ROOT) + doClassName.substring(1);
        String dtoClassParamName = dtoClassName.substring(0, 1).toLowerCase(Locale.ROOT) + dtoClassName.substring(1);
        String mapperClassParamName = mapperClassName.substring(0, 1).toLowerCase(Locale.ROOT) + mapperClassName.substring(1);
        
        JavaObjectBuilder builder = new JavaObjectBuilder(packageName, className);
        // 导入
        builder.addImport(interfaceBuilder.getReference());
        builder.addImport(doBuilder.getReference());
        builder.addImport(dtoBuilder.getReference());
        builder.addImport(mapperClassBuilder.getReference());
        builder.addImport("org.springframework.beans.factory.annotation.Autowired");
        builder.addImport("org.springframework.stereotype.Service");
        
        // 接口
        builder.addImplements(interfaceBuilder.getClassName());
        // 注解
        builder.addAnnotation("@Service");
        
        // mapper
        JavaField mapperField = new JavaField(Modifier.PRIVATE, mapperClassName, mapperClassBuilder.getReference(), mapperClassParamName);
        mapperField.addAnnotation("@Autowired");
        builder.addField(mapperField, false, false);
    
        // getDetail
        List<String> getBody = new LinkedList<>();
        getBody.add("return transfer(" + mapperField.getName() + ".select(transfer(dto)));");
        JavaMethod getDetail = new JavaMethod(false, Modifier.PUBLIC, dtoBuilder.getClassName(), "get", getBody);
        getDetail.addParam(dtoBuilder.getClassName(), "dto");
        getDetail.addAnnotation("@Override");
        builder.addMethod(getDetail);
        
        // add
        List<String> addBody = new LinkedList<>();
        addBody.add("return " + mapperField.getName() + ".insert(transfer(dto)) > 0;");
        JavaMethod add = new JavaMethod(false, Modifier.PUBLIC, "boolean", "add", addBody);
        add.addParam(dtoBuilder.getClassName(), "dto");
        add.addAnnotation("@Override");
        builder.addMethod(add);
        
        // delete
        List<String> deleteBody = new LinkedList<>();
        deleteBody.add("return " + mapperField.getName() + ".delete(transfer(dto)) > 0;");
        JavaMethod delete = new JavaMethod(false, Modifier.PUBLIC, "boolean", "delete", deleteBody);
        delete.addParam(dtoBuilder.getClassName(), "dto");
        delete.addAnnotation("@Override");
        builder.addMethod(delete);
        
        // update
        List<String> updateBody = new LinkedList<>();
        updateBody.add("return " + mapperField.getName() + ".update(transfer(dto)) > 0;");
        JavaMethod update = new JavaMethod(false, Modifier.PUBLIC, "boolean", "update", updateBody);
        update.addParam(dtoBuilder.getClassName(), "dto");
        update.addAnnotation("@Override");
        builder.addMethod(update);
        
        // dto转do
        List<JavaField> doFieldList = doBuilder.getFieldList();
        List<String> dtoToDoBody = new LinkedList<>();
        dtoToDoBody.add("if (dto == null) {");
        dtoToDoBody.add("\treturn null;");
        dtoToDoBody.add("}");
        dtoToDoBody.add(doClassName + " domain = new " + doClassName + "();");
        for (JavaField doField : doFieldList) {
            // 获取do的setter方法
            SetterMethod setterMethod = doBuilder.getSetterMethod(doField.getName());
            // 获取dto的getter方法
            GetterMethod getterMethod = dtoBuilder.getGetterMethod(doField.getName());
            if (setterMethod == null || getterMethod == null) {
                continue;
            }
            // 使用getter/setter方法构造
            if (setterMethod.getFieldTypeReference().equals(getterMethod.getReturnTypeReference())) {
                dtoToDoBody.add("domain." + setterMethod.getName() + "(dto." + getterMethod.getName() + "());");
            }
        }
        dtoToDoBody.add("return domain;");
        JavaMethod dtoToDo = new JavaMethod(false, Modifier.PRIVATE, doClassName, "transfer", dtoToDoBody);
        dtoToDo.addParam(dtoBuilder.getClassName(), "dto");
        builder.addMethod(dtoToDo);
        
        // do转dto
        List<JavaField> dtoFieldList = dtoBuilder.getFieldList();
        List<String> doToDtoBody = new LinkedList<>();
        doToDtoBody.add("if (domain == null) {");
        doToDtoBody.add("\treturn null;");
        doToDtoBody.add("}");
        doToDtoBody.add(dtoClassName + " dto = new " + dtoClassName + "();");
        for (JavaField dtoField : dtoFieldList) {
            // 获取dto的setter方法
            SetterMethod setterMethod = dtoBuilder.getSetterMethod(dtoField.getName());
            // 获取do的getter方法
            GetterMethod getterMethod = doBuilder.getGetterMethod(dtoField.getName());
            if (setterMethod == null || getterMethod == null) {
                continue;
            }
            // 使用getter/setter方法构造
            if (setterMethod.getFieldTypeReference().equals(getterMethod.getReturnTypeReference())) {
                doToDtoBody.add("dto." + setterMethod.getName() + "(domain." + getterMethod.getName() + "());");
            }
        }
        doToDtoBody.add("return dto;");
        JavaMethod doToDto = new JavaMethod(false, Modifier.PRIVATE, dtoClassName, "transfer", doToDtoBody);
        doToDto.addParam(doBuilder.getClassName(), "domain");
        builder.addMethod(doToDto);
        
        return builder;
    }
    
    private JavaObjectBuilder buildController(BuildConfig config, JavaObjectBuilder serviceBuilder, JavaObjectBuilder dtoBuilder) {
        JavaObjectBuilder builder = new JavaObjectBuilder(config.getControllerPackageName(), config.getControllerClassName());
        builder.addImport(serviceBuilder.getReference());
        builder.addImport(dtoBuilder.getReference());
        builder.addImport("org.springframework.beans.factory.annotation.Autowired");
        builder.addImport("org.springframework.web.bind.annotation.RestController");
        builder.addImport("org.springframework.web.bind.annotation.GetMapping");
        builder.addImport("org.springframework.web.bind.annotation.PostMapping");
        
        builder.addAnnotation("@RestController");
        if (config.getBusinessName() != null && !config.getBusinessName().equals("")) {
            builder.addImport("org.springframework.web.bind.annotation.RequestMapping");
            builder.addAnnotation("@RequestMapping(\"/" + config.getBusinessName() + "\")");
        }
    
        String serviceClassName = serviceBuilder.getClassName();
        String serviceClassParamName = serviceClassName.substring(0, 1).toLowerCase(Locale.ROOT) + serviceClassName.substring(1);
        JavaField service = new JavaField(Modifier.PRIVATE, serviceClassName, serviceBuilder.getReference(), serviceClassParamName);
        service.addAnnotation("@Autowired");
        builder.addField(service, false, false);
    
        // getDetail
        List<String> getBody = new LinkedList<>();
        getBody.add("return " + serviceClassParamName + ".get(dto);");
        JavaMethod getDetail = new JavaMethod(false, Modifier.PUBLIC, dtoBuilder.getClassName(), "get", getBody);
        getDetail.addParam(dtoBuilder.getClassName(), "dto");
        getDetail.addAnnotation("@GetMapping(\"/getDetail\")");
        builder.addMethod(getDetail);
    
        // add
        List<String> addBody = new LinkedList<>();
        addBody.add("return " + serviceClassParamName + ".add(dto);");
        JavaMethod add = new JavaMethod(false, Modifier.PUBLIC, "boolean", "add", addBody);
        add.addParam(dtoBuilder.getClassName(), "dto");
        add.addAnnotation("@PostMapping(\"/add\")");
        builder.addMethod(add);
    
        // delete
        List<String> deleteBody = new LinkedList<>();
        deleteBody.add("return " + serviceClassParamName + ".delete(dto);");
        JavaMethod delete = new JavaMethod(false, Modifier.PUBLIC, "boolean", "delete", deleteBody);
        delete.addParam(dtoBuilder.getClassName(), "dto");
        delete.addAnnotation("@PostMapping(\"/delete\")");
        builder.addMethod(delete);
    
        // update
        List<String> updateBody = new LinkedList<>();
        updateBody.add("return " + serviceClassParamName + ".update(dto);");
        JavaMethod update = new JavaMethod(false, Modifier.PUBLIC, "boolean", "update", updateBody);
        update.addParam(dtoBuilder.getClassName(), "dto");
        update.addAnnotation("@PostMapping(\"/update\")");
        builder.addMethod(update);
        
        return builder;
    }
    
    private String getDoc(ColumnDefinition columnDefinition) {
        List<String> columnSpecList = columnDefinition.getColumnSpecs();
        if (columnSpecList == null || columnSpecList.size() <= 0) {
            return null;
        }
        String annotationKeyword = "COMMENT";
        for (int i = 0; i < columnSpecList.size(); i++) {
            if (annotationKeyword.equals(columnSpecList.get(i).toUpperCase(Locale.ENGLISH))) {
                if (columnSpecList.size() > i + 1) {
                    return columnSpecList.get(i + 1).replace("'", "");
                } else {
                    return null;
                }
            }
        }
        return null;
    }
    
    private String getFieldName(String columnName) {
        return JavaField.transferName(columnName);
    }
    
    private TypeMap getFieldType(String dataType) {
        return JavaField.transferType(dataType);
    }
    
    private void saveFile(String basePath, String packageName, String fileName, String code) {
        String path;
        if (packageName != null) {
            path = packageName.replace(".", "/");
            path = basePath + "/" + path + "/" + fileName;
        } else {
            path = basePath + "/" + fileName;
        }
        FileUtil.writeFile(path, code);
    }
    
}
