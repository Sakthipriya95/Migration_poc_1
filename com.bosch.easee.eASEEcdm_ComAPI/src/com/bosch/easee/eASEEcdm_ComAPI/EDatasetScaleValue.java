package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

public enum EDatasetScaleValue implements ComEnum {
    DCComplete(1),
    DCPartition(2),
    DCIntegration(3),
    ;

    private final int value;
    EDatasetScaleValue(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
