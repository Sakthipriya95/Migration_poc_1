package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

public enum EOperationContext implements ComEnum {
    CDMOpIntegration(16),
    CDMOpExportIntegratedObjectFile(23),
    CDMOpExportIntegratedParameterSet(24),
    ;

    private final int value;
    EOperationContext(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
