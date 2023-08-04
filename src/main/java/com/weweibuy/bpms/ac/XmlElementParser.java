package com.weweibuy.bpms.ac;

import com.weweibuy.bpms.ac.model.AcMessageElement;
import com.weweibuy.bpms.ac.model.AcProcessDefinition;
import com.weweibuy.framework.common.core.exception.Exceptions;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * @author durenhao
 * @date 2023/8/1 18:11
 **/
public class XmlElementParser {

    private XmlElement rootXmlElement;

    private List<AcMessageElement> messageElementList;


    public XmlElementParser(XmlElement rootXmlElement) {
        this.rootXmlElement = rootXmlElement;
    }

    public List<AcProcessDefinition> parseProcessDefinition() {
        List<XmlElement> messageList = rootXmlElement.filterSubElementByName("message");

        List<XmlElement> processList = rootXmlElement.filterSubElementByName("process");


        return null;
    }


    public AcProcessDefinition parseSingleProcess(XmlElement processElement) {
        List<XmlElement> startEventList = processElement.filterSubElementByName("startEvent");
        if (CollectionUtils.isEmpty(startEventList) || startEventList.size() > 1) {
            throw Exceptions.business("只能有且一个开始事件");
        }
        XmlElement startEvent = startEventList.get(0);

        // outgoing 线条
        List<XmlElement> outgoingElementList = startEvent.filterSubElementByName("outgoing");



        return null;
    }

}
