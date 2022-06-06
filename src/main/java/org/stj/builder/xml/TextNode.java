package org.stj.builder.xml;

/**
 * @author godBless 2022/06/01
 * 文本node
 * label为纯文本
 */
public class TextNode extends XmlNode {
    
    public TextNode(String label) {
        super(label);
    }
    
    @Override
    String getCode(String linePrefix, int level) {
        return getRealLinePrefix(linePrefix, level) + element;
    }
}
