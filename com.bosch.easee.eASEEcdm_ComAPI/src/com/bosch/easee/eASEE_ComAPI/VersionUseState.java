package com.bosch.easee.eASEE_ComAPI  ;

import com4j.*;

public enum VersionUseState implements ComEnum {
    ustSubUndefined(-2),
    ustUndefined(-1),
    ustEnabled(0),
    ustDisabled(1),
    ustObsolete(2),
    ustSub_disabled(3),
    ustHidden(4),
    ;

    private final int value;
    VersionUseState(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
