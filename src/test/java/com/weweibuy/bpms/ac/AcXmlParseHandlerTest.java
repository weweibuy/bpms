package com.weweibuy.bpms.ac;

import com.weweibuy.framework.common.core.utils.ClassPathFileUtils;
import com.weweibuy.framework.common.core.utils.JackJsonUtils;
import org.camunda.bpm.engine.impl.bpmn.parser.BpmnParse;
import org.camunda.bpm.engine.impl.bpmn.parser.BpmnParser;
import org.camunda.bpm.engine.impl.util.xml.Element;
import org.camunda.bpm.engine.impl.util.xml.ParseHandler;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;

public class AcXmlParseHandlerTest {

    protected static final String EXTERNAL_GENERAL_ENTITIES = "http://xml.org/sax/features/external-general-entities";
    protected static final String DISALLOW_DOCTYPE_DECL = "http://apache.org/xml/features/disallow-doctype-decl";
    protected static final String LOAD_EXTERNAL_DTD = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
    protected static final String EXTERNAL_PARAMETER_ENTITIES = "http://xml.org/sax/features/external-parameter-entities";
    protected static final String NAMESPACE_PREFIXES = "http://xml.org/sax/features/namespace-prefixes";

    protected static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
    protected static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
    protected static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";

    protected static final String JAXP_ACCESS_EXTERNAL_SCHEMA = "http://javax.xml.XMLConstants/property/accessExternalSchema";
    protected static final String JAXP_ACCESS_EXTERNAL_SCHEMA_SYSTEM_PROPERTY = "javax.xml.accessExternalSchema";
    protected static final String JAXP_ACCESS_EXTERNAL_SCHEMA_ALL = "all";

    @Test
    public void test() throws ParserConfigurationException, SAXException, IOException {
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        saxParserFactory.setNamespaceAware(true);
        saxParserFactory.setFeature(NAMESPACE_PREFIXES, true);

        SAXParser saxParser = saxParserFactory.newSAXParser();


        InputStream inputStream = ClassPathFileUtils.classPathFileOrOther("classpath:event.bpmn");

        AcXmlParseHandler acXmlParseHandler = new AcXmlParseHandler();
        saxParser.parse(inputStream, acXmlParseHandler);

        XmlElement rootXmlElement = acXmlParseHandler.getRootXmlElement();
        String json = JackJsonUtils.writeValue(rootXmlElement);

        XmlElementParser xmlElementParser = new XmlElementParser(rootXmlElement);

        xmlElementParser.parseProcessDefinition();
    }

    @Test
    public void test2() throws Exception {
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        SAXParser saxParser = saxParserFactory.newSAXParser();
        InputStream inputStream = ClassPathFileUtils.classPathFileOrOther("classpath:event.bpmn");
        BpmnParser bpmnParser = new BpmnParser(null, null);

        BpmnParse bpmnParse = new BpmnParse(bpmnParser);
        ParseHandler parseHandler = new ParseHandler(bpmnParse);

        saxParser.parse(inputStream, parseHandler);
        Element rootElement = bpmnParse.getRootElement();

        System.err.println(rootElement);
    }

}