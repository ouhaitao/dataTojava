package org.stj.builder.xml;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * @author godBless 2022/06/01
 * xml节点
 * eg:
 * xml节点<select id="getDetail">select * from table</select>
 * label = select id="getDetail"
 * childList = {new TextNode("select * from table")}
 */
public class XmlNode {
    
    /**
     * 标签
     */
    protected String element;
    
    protected LinkedHashMap<String, String> properties;
    
    /**
     * 子标签
     */
    protected List<XmlNode> childList;
    
    public XmlNode(String element) {
        this.element = element;
        childList = new LinkedList<>();
        properties = new LinkedHashMap<>();
    }
    
    public void addChild(XmlNode node) {
        if (node == null) {
            throw new NullPointerException("node is null");
        }
        childList.add(node);
    }
    
    public void addProperty(String key, String value) {
        this.properties.put(key, value);
    }
    
    public List<XmlNode> getChildList() {
        return childList;
    }
    
    String getCode() {
        return getCode("", 1);
    }
    
    /**
     * 生成代码
     * @param linePrefix 行前缀
     * @param level 当前节点的层级
     */
    String getCode(String linePrefix, int level) {
        String realLinePrefix = getRealLinePrefix(linePrefix, level);
        StringBuilder code = new StringBuilder();
        if (childList.size() <= 0) {
            code.append(realLinePrefix).append("<").append(element);
            properties.forEach((key, value) -> code.append(" ").append(key).append("=\"").append(value).append("\""));
            code.append("/>");
            return code.toString();
        }
        // 本节点的element
        code.append(realLinePrefix).append("<").append(element);
        properties.forEach((key, value) -> code.append(" ").append(key).append("=\"").append(value).append("\""));
        code.append(">").append("\n");
        // 子节点
        childList.forEach(child -> code.append(child.getCode(linePrefix, level + 1)).append("\n"));
        code.append(realLinePrefix).append("</").append(element).append(">");
        return code.toString();
    }
    
    protected String getRealLinePrefix(String linePrefix, int level) {
        StringBuilder realLinePrefixBuilder = new StringBuilder();
        for (int i = 0; i < level; i++) {
            realLinePrefixBuilder.append(linePrefix);
        }
        return realLinePrefixBuilder.toString();
    }
}
