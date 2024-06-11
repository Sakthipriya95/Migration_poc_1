package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * ICDMDeliveryAssignment Interface
 */
@IID("{A6F442DE-850E-4824-9D8E-4E43987F637A}")
public interface ICDMDeliveryAssignment extends Com4jObject {
    /**
     * property Dataset
     */
    @VTID(7)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMDataset dataset();

    /**
     * property ExternalFile
     */
    @VTID(8)
    boolean externalFile();

    /**
     * property SourceFile
     */
    @VTID(9)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMDeliverySourceFile sourceFile();

    /**
     * property ActivateSource
     */
    @VTID(10)
    boolean activateSource();

    /**
     * property InternalSourceVersion
     */
    @VTID(11)
    int internalSourceVersion();

    /**
     * property DeliveryProperties
     */
    @VTID(12)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMDeliveryProperties deliveryProperties();

}
