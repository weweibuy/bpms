package com.weweibuy.bpms.ac;

import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.Attributes;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author durenhao
 * @date 2023/8/1 11:18
 **/
@Data
public class XmlElement {

    private String id;

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
            if (key.equals("id")) {
                xmlElement.setId(value);
            }
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


    public Map<String, XmlElement> elementIdMap() {
        List<XmlElement> xmlElements = allElementList();
        return xmlElements.stream()
                .filter(e -> StringUtils.isNotBlank(e.getId()))
                .collect(Collectors.toMap(XmlElement::getId, Function.identity()));
    }


    public static void elementList(XmlElement element, List<XmlElement> list) {
        list.add(element);
        List<XmlElement> subElementList = element.getSubElement();
        if (CollectionUtils.isNotEmpty(subElementList)) {
            subElementList.forEach(e -> elementList(e, list));
        }
    }

    public List<XmlElement> allElementList() {
        List<XmlElement> list = new ArrayList<>();
        elementList(this, list);
        return list;
    }

    public Map<String, List<XmlElement>> elementNameGroup() {
        List<XmlElement> xmlElements = allElementList();
        return xmlElements.stream()
                .filter(e -> StringUtils.isNotBlank(e.getName()))
                .collect(Collectors.groupingBy(XmlElement::getName));

    }


    public List<XmlElement> filterSubElementByName(String name) {
        if (CollectionUtils.isEmpty(subElement)) {
            return Collections.emptyList();
        }
        return subElement.stream()
                .filter(e -> name.equals(e.getName()))
                .collect(Collectors.toList());
    }

}
