package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

public enum EUserIntervention implements ComEnum {
    UserNone(0),
    UserOK(1),
    UserCancel(-1),
    UserAbort(-2),
    ;

    private final int value;
    EUserIntervention(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
