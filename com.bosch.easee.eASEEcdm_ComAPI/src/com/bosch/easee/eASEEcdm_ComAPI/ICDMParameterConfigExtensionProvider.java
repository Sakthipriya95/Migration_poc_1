package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

@IID("{317D365C-D992-49F9-B60B-CE0BC4F11997}")
public interface ICDMParameterConfigExtensionProvider extends Com4jObject {
    @VTID(7)
    boolean populateParameterDashboardPages(
        int pageNo,
        int versionNumber,
        int contextVersId,
        Holder<com.bosch.easee.eASEEcdm_ComAPI.IDlgPagePlugin> page);

}
