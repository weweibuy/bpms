package com.weweibuy.bpms.ac.model;

import lombok.Data;

/**
 * @author durenhao
 * @date 2023/8/1 17:38
 **/
@Data
public class AcProcessDefinition extends AcElement {

    private String version;

    private AcElement startElement;


}
