package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

public enum EDatasetType implements ComEnum {
    IntegrationDST(1),
    PartitionDST(2),
    FullDST(4),
    ;

    private final int value;
    EDatasetType(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
