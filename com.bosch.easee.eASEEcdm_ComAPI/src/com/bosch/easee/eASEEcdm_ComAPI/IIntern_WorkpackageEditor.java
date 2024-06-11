package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

@IID("{DFA3B890-17CE-41ED-B325-0EAB2C9440B6}")
public interface IIntern_WorkpackageEditor extends Com4jObject {
    @VTID(7)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMViewDefinition getViewDefinition(
        int iVersionNumber);

    @VTID(8)
    void createViewDefinition(
        com.bosch.easee.eASEEcdm_ComAPI.ICDMCreateViewDefinitionSettings iSettings,
        Holder<com.bosch.easee.eASEEcdm_ComAPI.ICDMViewDefinition> oViewDefinition,
        Holder<com.bosch.easee.eASEEcdm_ComAPI.ICDMProcessInfo> oInfo);

    @VTID(9)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCreateViewDefinitionSettings getCreateViewDefinitionSettings();

}
