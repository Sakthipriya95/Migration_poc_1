package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * ICDMCreateDatasetRevisionVariantResult Interface
 */
@IID("{1E9811E6-964D-4252-BB09-842365543D52}")
public interface ICDMCreateDatasetRevisionVariantResult extends com.bosch.easee.eASEEcdm_ComAPI.ICDMProcessInfo {
    /**
     * property Result
     */
    @VTID(11)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMDatasetCol createdDataSets();

    @VTID(11)
    @ReturnValue(type=NativeType.VARIANT,defaultPropertyThrough={com.bosch.easee.eASEEcdm_ComAPI.ICDMDatasetCol.class})
    java.lang.Object createdDataSets(
        int index);

    /**
     * Messages
     */
    @VTID(12)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMMessageCol messages();

    @VTID(12)
    @ReturnValue(type=NativeType.VARIANT,defaultPropertyThrough={com.bosch.easee.eASEEcdm_ComAPI.ICDMMessageCol.class})
    java.lang.Object messages(
        int index);

}
