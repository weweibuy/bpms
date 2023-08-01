package com.weweibuy.bpms.ac.model;

import lombok.Data;

/**
 * @author durenhao
 * @date 2023/8/1 17:51
 **/
@Data
public class AcBodyElement extends AcElement {

    private String type;

    private AcFlowElement from;

    private AcFlowElement to;


}
