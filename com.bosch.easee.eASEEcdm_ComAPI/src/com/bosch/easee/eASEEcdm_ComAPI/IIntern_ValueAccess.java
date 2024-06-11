package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

@IID("{D74CA29D-910E-47E6-A81A-A5090A052BC7}")
public interface IIntern_ValueAccess extends Com4jObject {
    @VTID(7)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMQueryValuesSetting getQueryValuesSetting();

    @VTID(8)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection getValues(
        int datasetVersion,
        com.bosch.easee.eASEEcdm_ComAPI.ICDMQueryValuesSetting queryValuesSetting);

}
