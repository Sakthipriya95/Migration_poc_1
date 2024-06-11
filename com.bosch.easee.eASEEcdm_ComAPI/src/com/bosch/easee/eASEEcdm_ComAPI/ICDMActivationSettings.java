package com.bosch.easee.eASEEcdm_ComAPI;

import com4j.*;


/**
 * ICDMActivationSettings Interface
 */
@IID("{5C702136-44F9-47E8-81BD-3114D4A724E6}")
public interface ICDMActivationSettings extends Com4jObject,
    Iterable<Com4jObject> {
    @VTID(7)
    java.util.Iterator<Com4jObject> iterator();

    @VTID(8)
    @DefaultMethod
    @ReturnValue(type = NativeType.VARIANT)
    java.lang.Object item(int index);

    @VTID(9)
    int count();

    @VTID(10)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMActivationAssignment addAssignment(
        com.bosch.easee.eASEEcdm_ComAPI.ICDMDataset iDataSet,
        @MarshalAs(NativeType.Dispatch)
    com4j.Com4jObject iActivationItem, boolean iActivate);

    /**
     * property RequiredDataQuality
     */
    @VTID(11)
    void requiredDataQuality(
        com.bosch.easee.eASEEcdm_ComAPI.ECdmRequiredDataQuality newVal);

    /**
     * property RequiredDataQuality
     */
    @VTID(12)
    com.bosch.easee.eASEEcdm_ComAPI.ECdmRequiredDataQuality requiredDataQuality();
}
