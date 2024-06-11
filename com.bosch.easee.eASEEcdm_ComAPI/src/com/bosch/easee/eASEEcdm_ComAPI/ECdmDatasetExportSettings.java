package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

public enum ECdmDatasetExportSettings implements ComEnum {
    CDM_EXPORT_SETTING_NONE(0),
    CDM_EXPORT_SETTING_KEEPA2L_FILE(1),
    CDM_EXPORT_SETTING_NOSUBFOLDER(2),
    CDM_EXPORT_SETTING_NOVARIANTSCHECKS_IFEMPTY(4),
    CDM_EXPORT_SETTING_VALIDATION_ONLY(8),
    ;

    private final int value;
    ECdmDatasetExportSettings(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
