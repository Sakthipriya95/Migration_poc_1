package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * ICDMSoftwareChangeResult Interface
 */
@IID("{C4966B16-1663-4A37-A853-E18EE710E58C}")
public interface ICDMSoftwareChangeResult extends com.bosch.easee.eASEEcdm_ComAPI.ICDMProcessInfo {
    /**
     * property Result
     */
    @VTID(11)
    com.bosch.easee.eASEEcdm_ComAPI.ECdmValidationResult validationResult();

    /**
     * property Result
     */
    @VTID(12)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMDatasetCol createdDataSets();

    @VTID(12)
    @ReturnValue(type=NativeType.VARIANT,defaultPropertyThrough={com.bosch.easee.eASEEcdm_ComAPI.ICDMDatasetCol.class})
    java.lang.Object createdDataSets(
        int index);

    /**
     * ValidationMessages
     */
    @VTID(13)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMMessageCol validationMessages();

    @VTID(13)
    @ReturnValue(type=NativeType.VARIANT,defaultPropertyThrough={com.bosch.easee.eASEEcdm_ComAPI.ICDMMessageCol.class})
    java.lang.Object validationMessages(
        int index);

    /**
     * CreationMessages
     */
    @VTID(14)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMMessageCol creationMessages();

    @VTID(14)
    @ReturnValue(type=NativeType.VARIANT,defaultPropertyThrough={com.bosch.easee.eASEEcdm_ComAPI.ICDMMessageCol.class})
    java.lang.Object creationMessages(
        int index);

    /**
     * GetMergeLog
     */
    @VTID(15)
    java.lang.String getMergeLog(
        com.bosch.easee.eASEEcdm_ComAPI.ICDMDataset dataset,
        boolean iTakeOwnerShip);

    /**
     * GetWorksplitLog
     */
    @VTID(16)
    java.lang.String getWorksplitLog(
        com.bosch.easee.eASEEcdm_ComAPI.ICDMDataset dataset,
        boolean iTakeOwnerShip);

}
