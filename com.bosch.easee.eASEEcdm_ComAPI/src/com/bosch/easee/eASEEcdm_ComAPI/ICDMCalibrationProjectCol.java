package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * ICDMCalibrationProjectCol Interface
 */
@IID("{31D829A3-1202-4FFC-A2D2-43574D33271E}")
public interface ICDMCalibrationProjectCol extends Com4jObject,Iterable<Com4jObject> {
    @VTID(7)
    java.util.Iterator<Com4jObject> iterator();

    @VTID(8)
    @DefaultMethod
    @ReturnValue(type=NativeType.VARIANT)
    java.lang.Object item(
        int index);

    @VTID(9)
    int count();

    /**
     * method Add
     */
    @VTID(10)
    void add(
        com.bosch.easee.eASEEcdm_ComAPI.ICDMCalibrationProject pInterface);

}
