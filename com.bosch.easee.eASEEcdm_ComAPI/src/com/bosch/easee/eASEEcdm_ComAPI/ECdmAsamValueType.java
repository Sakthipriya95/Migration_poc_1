package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

public enum ECdmAsamValueType implements ComEnum {
    CDM_ASAM_VALUE_DOUBLE(2),
    CDM_ASAM_VALUE_STRING(3),
    CDM_ASAM_VALUE_BYTE(4),
    ;

    private final int value;
    ECdmAsamValueType(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
