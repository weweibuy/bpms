package com.weweibuy.bpms.ac;

import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.Attributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author durenhao
 * @date 2023/8/1 11:18
 **/
@Data
public class XmlElement {

    private String namespace;

    private String name;

    private String text;

    private Map<String, String> attrMap;

    private List<XmlElement> subElement;

    public static XmlElement fromReadInfo(String localName, String qName, Attributes attributes) {
        XmlElement xmlElement = new XmlElement();
        xmlElement.setName(localName);
        int index = qName.indexOf(':');
        if (index != -1) {
            xmlElement.setNamespace(qName.substring(0, index));
        }
        for (int i = 0; i < attributes.getLength(); i++) {
            String key = attributes.getQName(i);
            String value = attributes.getValue(i);
            xmlElement.addAttr(key, value);
        }
        return xmlElement;
    }

    public void addElement(XmlElement xmlElement) {
        if (CollectionUtils.isEmpty(subElement)) {
            subElement = new ArrayList<>();
        }
        subElement.add(xmlElement);
    }

    public void addAttr(String key, String value) {
        if (MapUtils.isEmpty(attrMap)) {
            attrMap = new HashMap<>();
        }
        attrMap.put(key, value);
    }


    public void appendText(String text) {
        if (StringUtils.isBlank(text)) {
            return;
        }
        if (this.text == null) {
            this.text = text;
        } else {
            this.text += text;
        }
    }
}
