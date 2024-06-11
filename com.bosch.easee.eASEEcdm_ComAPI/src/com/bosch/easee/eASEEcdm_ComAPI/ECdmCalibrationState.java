package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

public enum ECdmCalibrationState implements ComEnum {
    CDM_CALIBSTATENOTHING(1),
    CDM_CALIBSTATECHANGED(2),
    CDM_CALIBSTATEPRELIMCALIBRATED(3),
    CDM_CALIBSTATECALIBRATED(4),
    CDM_CALIBSTATECHECKED(5),
    CDM_CALIBSTATECOMPLETED(6),
    ;

    private final int value;
    ECdmCalibrationState(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
