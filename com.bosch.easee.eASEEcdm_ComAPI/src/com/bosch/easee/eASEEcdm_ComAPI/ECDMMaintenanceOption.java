package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

public enum ECDMMaintenanceOption implements ComEnum {
    CDMMaintenanceDefinitionData(1),
    CDMMaintenanceValueData(2),
    CDMMaintenanceAttributes(4),
    CDMMaintenanceProgramKeys(8),
    ;

    private final int value;
    ECDMMaintenanceOption(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
