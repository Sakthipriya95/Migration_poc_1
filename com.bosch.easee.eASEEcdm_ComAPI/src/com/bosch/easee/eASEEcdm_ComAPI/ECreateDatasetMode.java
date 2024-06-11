package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

public enum ECreateDatasetMode implements ComEnum {
    CDMFromFile(1),
    CDMFromProgramset(2),
    CDMConfiguredProgramKey(3),
    ;

    private final int value;
    ECreateDatasetMode(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
