package com.bosch.easee.eASEEcdm_ComAPI;

import com4j.*;


/**
 * ICDMReplaceVersionsSettings Interface
 */
@IID("{7FE60160-4CF4-4F88-95EF-B7FCEE74763A}")
public interface ICDMReplaceVersionsSettings extends Com4jObject {
    /**
     * method Add
     */
    @VTID(7)
    void add(int container, int versionToReplace, int replacement);

    /**
     * method Replacements
     */
    @VTID(8)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection replacements();

    @VTID(8)
    @ReturnValue(type = NativeType.VARIANT, defaultPropertyThrough =  {
        com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection.class}
    )
    java.lang.Object replacements(int index);

    /**
     * property RequiredDataQuality
     */
    @VTID(9)
    void requiredDataQuality(
        com.bosch.easee.eASEEcdm_ComAPI.ECdmRequiredDataQuality newVal);

    /**
     * property RequiredDataQuality
     */
    @VTID(10)
    com.bosch.easee.eASEEcdm_ComAPI.ECdmRequiredDataQuality requiredDataQuality();
}
