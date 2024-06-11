package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * ICdmInvokeCommand Interface
 */
@IID("{E679FF6A-7CD7-40AC-BF0A-BBCC5621E3CA}")
public interface ICdmInvokeCommand extends Com4jObject {
    /**
     * method IsLegal
     */
    @VTID(7)
    boolean isLegal(
        int commandId,
        int selectedVersion);

    /**
     * method Invoke
     */
    @VTID(8)
    void invoke(
        int commandId,
        com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection selObjectsWithContext);

    /**
     * method Commands
     */
    @VTID(9)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection commands();

    @VTID(9)
    @ReturnValue(type=NativeType.VARIANT,defaultPropertyThrough={com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection.class})
    java.lang.Object commands(
        int index);

}
