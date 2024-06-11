package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

public enum EDeliverCreationMode implements ComEnum {
    DCMAutomatical(1),
    DCMVariant(2),
    DCMRevision(3),
    ;

    private final int value;
    EDeliverCreationMode(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
