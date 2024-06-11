package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

public enum ECdmDatasetExportType implements ComEnum {
    CDM_EXPORT_OBJECTFILE(1),
    CDM_EXPORT_COMPLETE_PARAMETERSET(2),
    ;

    private final int value;
    ECdmDatasetExportType(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
