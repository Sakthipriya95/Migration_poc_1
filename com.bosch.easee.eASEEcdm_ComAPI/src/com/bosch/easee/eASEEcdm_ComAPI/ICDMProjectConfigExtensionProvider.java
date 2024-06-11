package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

@IID("{C6140B0C-376B-4CEE-BE88-CBEABBE04389}")
public interface ICDMProjectConfigExtensionProvider extends Com4jObject {
    @VTID(7)
    boolean populateProjectConfigPages(
        int pageNo,
        com.bosch.easee.eASEEcdm_ComAPI.ICDMCalibrationProject project,
        Holder<com.bosch.easee.eASEEcdm_ComAPI.IDlgPagePlugin> page,
        Holder<com.bosch.easee.eASEEcdm_ComAPI.ICDMProjectConfigExtension> docEvents);

    @VTID(8)
    boolean populateProjectDashboardPages(
        int pageNo,
        com.bosch.easee.eASEEcdm_ComAPI.ICDMCalibrationProject project,
        Holder<com.bosch.easee.eASEEcdm_ComAPI.IDlgPagePlugin> page,
        Holder<com.bosch.easee.eASEEcdm_ComAPI.ICDMProjectConfigExtension> docEvents);

}
