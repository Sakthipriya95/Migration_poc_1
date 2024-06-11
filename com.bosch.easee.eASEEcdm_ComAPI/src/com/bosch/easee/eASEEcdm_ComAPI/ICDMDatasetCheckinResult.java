package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * ICDMDatasetCheckinResult Interface
 */
@IID("{03BA7E9A-B988-41D1-ABB4-B3E7481373A0}")
public interface ICDMDatasetCheckinResult extends Com4jObject {
    /**
     * GetDatasetResult
     */
    @VTID(7)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMDatasetCheckinDatasetResult getDatasetResult(
        com.bosch.easee.eASEEcdm_ComAPI.ICDMDataset pDataset);

    /**
     * GetDatasetResultByVersionNumber
     */
    @VTID(8)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMDatasetCheckinDatasetResult getDatasetResultByVersionNumber(
        int versionNumber);

}
