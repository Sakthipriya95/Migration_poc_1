package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

@IID("{5407704E-04BE-4C6B-883C-368C8B1EF5D1}")
public interface ICDMPersonalSettingsExtensionProvider extends Com4jObject {
    @VTID(7)
    boolean populatePersonalSettingsPage(
        int pageNo,
        Holder<com.bosch.easee.eASEEcdm_ComAPI.IDlgPagePlugin> page);

}
