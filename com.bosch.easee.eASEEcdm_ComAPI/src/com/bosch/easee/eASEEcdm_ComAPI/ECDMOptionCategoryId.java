package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

public enum ECDMOptionCategoryId implements ComEnum {
    CDMOptCat_Validation(1),
    CDMOptCat_CopyOptions(2),
    CDMOptCat_DmmOptions(3),
    ;

    private final int value;
    ECDMOptionCategoryId(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
