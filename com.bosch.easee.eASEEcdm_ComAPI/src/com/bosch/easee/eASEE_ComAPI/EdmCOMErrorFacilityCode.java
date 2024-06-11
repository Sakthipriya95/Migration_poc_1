package com.bosch.easee.eASEE_ComAPI  ;

import com4j.*;

public enum EdmCOMErrorFacilityCode implements ComEnum {
    ecfEdmOracle(40),
    ecfEdmDatabase(41),
    ecfEdmClient(42),
    ;

    private final int value;
    EdmCOMErrorFacilityCode(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
