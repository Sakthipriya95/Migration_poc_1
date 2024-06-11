package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * ICDMCreateDatasetResult Interface
 */
@IID("{1A6BC07C-861C-43EC-B393-1A22EC9F88E8}")
public interface ICDMCreateDatasetResult extends com.bosch.easee.eASEEcdm_ComAPI.ICDMProcessInfo {
    /**
     * property Result
     */
    @VTID(11)
    com.bosch.easee.eASEEcdm_ComAPI.ECdmValidationResult validationResult();

    /**
     * property CreatedDataSets
     */
    @VTID(12)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMDatasetCol createdDataSets();

    @VTID(12)
    @ReturnValue(type=NativeType.VARIANT,defaultPropertyThrough={com.bosch.easee.eASEEcdm_ComAPI.ICDMDatasetCol.class})
    java.lang.Object createdDataSets(
        int index);

    /**
     * medthod GetExtractionLog
     */
    @VTID(13)
    java.lang.String getExtractionLog(
        com.bosch.easee.eASEEcdm_ComAPI.ICDMDataset dataset,
        boolean iTakeOwnerShip);

    /**
     * ValidationMessages
     */
    @VTID(14)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMMessageCol validationMessages();

    @VTID(14)
    @ReturnValue(type=NativeType.VARIANT,defaultPropertyThrough={com.bosch.easee.eASEEcdm_ComAPI.ICDMMessageCol.class})
    java.lang.Object validationMessages(
        int index);

    /**
     * CreationMessages
     */
    @VTID(15)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMMessageCol creationMessages();

    @VTID(15)
    @ReturnValue(type=NativeType.VARIANT,defaultPropertyThrough={com.bosch.easee.eASEEcdm_ComAPI.ICDMMessageCol.class})
    java.lang.Object creationMessages(
        int index);

}
