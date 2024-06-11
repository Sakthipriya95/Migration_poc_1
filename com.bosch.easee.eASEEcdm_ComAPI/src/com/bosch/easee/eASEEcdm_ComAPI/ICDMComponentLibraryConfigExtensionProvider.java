package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

@IID("{3AB8926C-B307-4F1E-867A-276BB97272DD}")
public interface ICDMComponentLibraryConfigExtensionProvider extends Com4jObject {
    @VTID(7)
    boolean populateComponentLibraryDashboardPages(
        int pageNo,
        int versionNumber,
        int contextVersId,
        Holder<com.bosch.easee.eASEEcdm_ComAPI.IDlgPagePlugin> page);

}
