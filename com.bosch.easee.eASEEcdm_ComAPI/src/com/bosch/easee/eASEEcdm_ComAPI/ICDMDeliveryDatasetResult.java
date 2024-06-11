package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * ICDMDeliveryDatasetResult Interface
 */
@IID("{B2A97062-150B-4711-996E-FC63C29B3A02}")
public interface ICDMDeliveryDatasetResult extends Com4jObject {
    /**
     * property CreatedObjects
     */
    @VTID(7)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection createdObjects();

    @VTID(7)
    @ReturnValue(type=NativeType.VARIANT,defaultPropertyThrough={com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection.class})
    java.lang.Object createdObjects(
        int index);

    /**
     * property GetCopyLog
     */
    @VTID(8)
    java.lang.String getCopyLog(
        boolean iTakeOwnerShip);

    /**
     * property Dataset
     */
    @VTID(9)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMDataset dataset();

}
