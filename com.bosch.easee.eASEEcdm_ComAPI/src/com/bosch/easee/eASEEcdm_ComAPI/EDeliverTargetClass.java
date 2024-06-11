package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

public enum EDeliverTargetClass implements ComEnum {
    DTCEXD(1),
    DTCZFPAR(2),
    DTCFPAR(3),
    DTCEPAR(4),
    DTCPAR(5),
    DTCAUTO(6),
    DTCHEX(7),
    ;

    private final int value;
    EDeliverTargetClass(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
