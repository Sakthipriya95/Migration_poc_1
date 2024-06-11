package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

@IID("{27DC63B0-F059-4130-837F-E87AFA457F12}")
public interface IIntern_GenericObjectHandling extends Com4jObject {
    @VTID(7)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMReplaceVersionsSettings getReplaceVersionsSettings();

    @VTID(8)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMReplaceVersionsResult replaceVersions(
        com.bosch.easee.eASEEcdm_ComAPI.ICDMReplaceVersionsSettings replaceVersionsSettings);

    @VTID(9)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection getParametersetUsages(
        int paramset,
        int filter);

}
