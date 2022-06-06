package org.dtj.builder.xml;

import java.util.LinkedList;
import java.util.List;

/**
 * @author godBless 2022/06/01
 * xml生成器
 */
public class XmlBuilder {

    private List<XmlNode> headerList;
    private XmlNode root;
    
    public XmlBuilder(XmlNode root) {
        this.root = root;
        this.headerList = new LinkedList<>();
    }
    
    public void addHeaderNode(XmlNode headerNode) {
        this.headerList.add(headerNode);
    }
    
    /**
     * 生成代码
     */
    public String getCode() {
        RootNode rootNode = new RootNode(root);
        return rootNode.getCode("\t", 0);
    }
    
    /**
     * XML的根节点包装类,方便getCode
     * 重写getCode
     */
    private final class RootNode extends XmlNode {
    
        private XmlNode rootNode;
        
        public RootNode(XmlNode rootNode) {
            super("");
            this.rootNode = rootNode;
        }
    
        @Override
        public void addChild(XmlNode node) {
            throw new UnsupportedOperationException("RootNode can't add Child");
        }
    
        @Override
        public List<XmlNode> getChildList() {
            throw new UnsupportedOperationException("RootNode can't get Child");
        }
    
        /**
         * 与父类区别在于当前节点输出代码不用linePrefix
         * @param linePrefix 行前缀
         */
        @Override
        String getCode(String linePrefix, int level) {
            StringBuilder code = new StringBuilder();
            // header节点
            headerList.forEach(headerNode -> code.append(headerNode.getCode()).append("\n"));
            
            if (rootNode.childList.size() <= 0) {
                code.append("<").append(rootNode.element).append("/>");
                return code.toString();
            }
    
            code.append("<").append(rootNode.element);
            rootNode.properties.forEach((key, value) -> code.append(" ").append(key).append("=\"").append(value).append("\""));
            code.append(">").append("\n");
            rootNode.childList.forEach(child -> code.append(child.getCode(linePrefix, 1)).append("\n\n"));
            code.append("</").append(rootNode.element).append(">");
            return code.toString();
        }
    }
}