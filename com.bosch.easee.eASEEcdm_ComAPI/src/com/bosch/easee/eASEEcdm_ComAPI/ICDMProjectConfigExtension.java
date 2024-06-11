package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

@IID("{D3CACACC-D407-4BD1-B434-BB4ADB407E9B}")
public interface ICDMProjectConfigExtension extends Com4jObject {
        @VTID(8)
        void programKeysRenamed(
            com.bosch.easee.eASEEcdm_ComAPI.ICDMProgramKey newKey,
            java.lang.String oldName);

            @VTID(10)
            void productKeysRenamed(
                com.bosch.easee.eASEEcdm_ComAPI.ICDMProductKey newKey,
                java.lang.String oldName);

        }
