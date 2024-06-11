package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

@IID("{7AB52BD9-9DEF-48FB-9FA3-88A54268BCAD}")
public interface ICDMDomainSettingsExtensionProvider extends Com4jObject {
    @VTID(7)
    boolean populateDomainSettingsPage(
        int pageNo,
        Holder<com.bosch.easee.eASEEcdm_ComAPI.IDlgPagePlugin> page);

}
