package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

public enum ECdmFileTypelist implements ComEnum {
    CDM_TYPELIST_OBJECTFILES(1),
    CDM_TYPELIST_PHYSICALFILES(2),
    CDM_TYPELIST_DATAPARAMETERFORMATS(3),
    CDM_TYPELIST_MODELPARAMETERFORMATS(4),
    CDM_TYPELIST_ALLPARAMETERFILEFORMATS(5),
    CDM_TYPELIST_ALLFILEFORMATS(6),
    CDM_TYPELIST_DESCRIPTIONFILEFORMATS(7),
    CDM_TYPELIST_DELIVERYABLE(8),
    ;

    private final int value;
    ECdmFileTypelist(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
