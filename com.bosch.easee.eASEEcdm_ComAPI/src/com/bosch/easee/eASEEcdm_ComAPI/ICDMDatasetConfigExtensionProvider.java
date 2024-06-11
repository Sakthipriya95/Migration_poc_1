package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

@IID("{ED85B537-926C-4800-8151-3BBFB4BC607E}")
public interface ICDMDatasetConfigExtensionProvider extends Com4jObject {
    @VTID(7)
    boolean populateDatasetConfigPages(
        int pageNo,
        int dstVersId,
        Holder<com.bosch.easee.eASEEcdm_ComAPI.IDlgPagePlugin> page);

    @VTID(8)
    boolean populateDatasetDashboardPages(
        int pageNo,
        int dstVersId,
        Holder<com.bosch.easee.eASEEcdm_ComAPI.IDlgPagePlugin> page);

}
