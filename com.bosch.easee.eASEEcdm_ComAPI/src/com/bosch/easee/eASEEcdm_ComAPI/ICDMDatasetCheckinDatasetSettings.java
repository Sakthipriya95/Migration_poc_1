package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * ICDMDatasetCheckinDatasetSettings Interface
 */
@IID("{4AEEC9FD-89B3-442C-9CED-9C3DE0FE9D0B}")
public interface ICDMDatasetCheckinDatasetSettings extends Com4jObject {
    /**
     * property Comment
     */
    @VTID(7)
    void comment(
        java.lang.String comment);

    /**
     * property Comment
     */
    @VTID(8)
    java.lang.String comment();

    /**
     * property ObjectName
     */
    @VTID(9)
    void objectName(
        java.lang.String oHexVersionName);

    /**
     * property ObjectName
     */
    @VTID(10)
    java.lang.String objectName();

    /**
     * property VariantsForParameterCheck
     */
    @VTID(11)
    void variantsForParameterCheck(
        com.bosch.easee.eASEEcdm_ComAPI.ICDMCriterionVariants iInterface);

    /**
     * property VariantsForParameterCheck
     */
    @VTID(12)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCriterionVariants variantsForParameterCheck();

}
