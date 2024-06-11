package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

public enum ECreateViewDefinitionMode implements ComEnum {
    CVDEmpty(1),
    CVDByAsapAddressRange(2),
    CVDByImport(3),
    ;

    private final int value;
    ECreateViewDefinitionMode(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
