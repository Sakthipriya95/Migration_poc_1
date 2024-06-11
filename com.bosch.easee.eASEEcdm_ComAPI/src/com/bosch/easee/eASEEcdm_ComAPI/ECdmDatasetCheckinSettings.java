package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

public enum ECdmDatasetCheckinSettings implements ComEnum {
    CDM_CHECKIN_SETTING_NONE(0),
    CDM_CHECKIN_SETTING_NOVARIANTSCHECKS_IFEMPTY(4),
    CDM_CHECKIN_SETTING_VALIDATION_ONLY(8),
    CDM_CHECKIN_SETTING_SKIPHEXINTEGRATION(16),
    ;

    private final int value;
    ECdmDatasetCheckinSettings(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
