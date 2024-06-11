package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

public enum ECDMOptionLevel implements ComEnum {
    CDMOptSrcDomain(1),
    CDMOptSrcProject(2),
    CDMOptSrcTask(3),
    ;

    private final int value;
    ECDMOptionLevel(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
