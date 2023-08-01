package com.weweibuy.bpms.ac;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * @author durenhao
 * @date 2023/8/1 11:41
 **/
public class AcXmlParseHandler extends DefaultHandler {

    protected Locator locator;

    protected Deque<XmlElement> elementStack = new ArrayDeque<>();

    private XmlElement rootXmlElement;

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        XmlElement element =
                XmlElement.fromReadInfo(localName, qName, attributes);
        if (elementStack.isEmpty()) {
            rootXmlElement = element;
        } else {
            elementStack.peek().addElement(element);
        }
        elementStack.push(element);
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        elementStack.peek()
                .appendText(String.valueOf(ch, start, length));
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        elementStack.pop();
    }

    public void error(SAXParseException e) {
    }

    public void fatalError(SAXParseException e) {
    }

    public void warning(SAXParseException e) {
    }

    public void setDocumentLocator(Locator locator) {
        this.locator = locator;
    }

    public XmlElement getRootXmlElement() {
        return rootXmlElement;
    }
}
