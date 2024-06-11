package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

public enum EProcessState implements ComEnum {
    StateSuccess(0),
    StateError(-1),
    StateUserAbort(1),
    StateValidationFailed(-2),
    ;

    private final int value;
    EProcessState(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
