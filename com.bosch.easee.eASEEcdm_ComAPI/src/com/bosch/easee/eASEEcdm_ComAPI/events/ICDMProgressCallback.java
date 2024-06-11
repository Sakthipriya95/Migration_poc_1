package com.bosch.easee.eASEEcdm_ComAPI.events;

import com4j.*;

/**
 * ICDMProgressCallback
 */
@IID("{9BD45657-DB77-48AC-809C-CE0018F55897}")
public abstract class ICDMProgressCallback {
    /**
     * method OnCallbackInfo
     */
    @DISPID(1)
    public void onCallbackInfo(
        com.bosch.easee.eASEEcdm_ComAPI.ECallbackCategory iCategory,
        java.lang.String iCallbackInfo) {
            throw new UnsupportedOperationException();
    }

    /**
     * method OnProgress
     */
    @DISPID(2)
    public void onProgress(
        java.lang.String iOperation,
        int iProgressPercentage) {
            throw new UnsupportedOperationException();
    }

}
