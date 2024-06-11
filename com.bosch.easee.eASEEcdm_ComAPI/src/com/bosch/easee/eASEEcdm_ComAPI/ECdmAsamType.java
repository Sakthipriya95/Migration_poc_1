package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

public enum ECdmAsamType implements ComEnum {
    CDM_ASAM_TYPE_VALUE(1),
    CDM_ASAM_TYPE_ASCII(2),
    CDM_ASAM_TYPE_CURVE(3),
    CDM_ASAM_TYPE_MAP(4),
    CDM_ASAM_TYPE_CUBOID(5),
    CDM_ASAM_TYPE_AXIS(6),
    CDM_ASAM_TYPE_VAL_BLK(7),
    CDM_ASAM_TYPE_CUBE4(8),
    CDM_ASAM_TYPE_CUBE5(9),
    CDM_ASAM_TYPE_BLOB(10),
    CDM_ASAM_TYPE_UNKNOWN(0),
    ;

    private final int value;
    ECdmAsamType(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
