package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

@IID("{5B7DEBF1-68AC-4013-97ED-FE0986EE839F}")
public interface IIntern_IntegrationAccess extends Com4jObject {
    @VTID(7)
    void getA2LWithViewsAndRights(
        com.bosch.easee.eASEEcdm_ComAPI.ICDMDataset dataset,
        java.lang.String fileName);

}
