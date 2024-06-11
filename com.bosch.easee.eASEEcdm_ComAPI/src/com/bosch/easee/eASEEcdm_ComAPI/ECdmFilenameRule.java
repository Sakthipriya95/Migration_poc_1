package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

public enum ECdmFilenameRule implements ComEnum {
    CDM_FILENAME_SYSTEM(1),
    CDM_FILENAME_ORIGINAL(2),
    CDM_FILENAME_EASEE(3),
    ;

    private final int value;
    ECdmFilenameRule(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
