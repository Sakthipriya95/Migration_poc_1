package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

public enum ECdmFileTypeFeature implements ComEnum {
    CDM_TYPELIST_FORMATOPT_CALSTATE(1),
    CDM_TYPELIST_FORMATOPT_CREATABLE(2),
    CDM_TYPELIST_FORMATOPT_PHYSVALUE(4),
    CDM_TYPELIST_FORMATOPT_FUNCTIONS(8),
    CDM_TYPELIST_FORMATOPT_FUNCVERS(16),
    CDM_TYPELIST_FORMATOPT_GROUPS(128),
    CDM_TYPELIST_FORMATOPT_VARIANT_CRITERIONS(256),
    CDM_TYPELIST_FORMATOPT_ASAP_TYPE(512),
    ;

    private final int value;
    ECdmFileTypeFeature(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
