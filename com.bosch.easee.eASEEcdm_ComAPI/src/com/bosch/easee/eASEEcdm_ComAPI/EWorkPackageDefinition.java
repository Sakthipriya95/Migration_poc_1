package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

public enum EWorkPackageDefinition implements ComEnum {
    WPDByAsapFunction(1),
    WPDByViewDefinition(2),
    WPDNone(3),
    ;

    private final int value;
    EWorkPackageDefinition(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
