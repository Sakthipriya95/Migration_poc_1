spool c:\temp\50_Grants.log

------------------------------------------------------------------------------------------------------------------
-- ALM Task ID : 463370
-- Grants of A2L Responsibility tables to the JPA user
------------------------------------------------------------------------------------------------------------------
GRANT DELETE, INSERT, SELECT, UPDATE ON T_A2L_RESPONSIBILITY TO DGS_ICDM_JPA;
GRANT DELETE, INSERT, SELECT, UPDATE ON T_A2L_WORK_PACKAGES TO DGS_ICDM_JPA;
GRANT DELETE, INSERT, SELECT, UPDATE ON T_A2L_WP_DEFN_VERSIONS TO DGS_ICDM_JPA;
GRANT DELETE, INSERT, SELECT, UPDATE ON T_A2L_VARIANT_GROUPS TO DGS_ICDM_JPA;
GRANT DELETE, INSERT, SELECT, UPDATE ON T_A2L_VARGRP_VARIANT_MAPPING TO DGS_ICDM_JPA;
GRANT DELETE, INSERT, SELECT, UPDATE ON T_A2L_WP_RESPONSIBILITY TO DGS_ICDM_JPA;
GRANT DELETE, INSERT, SELECT, UPDATE ON T_A2L_WP_PARAM_MAPPING TO DGS_ICDM_JPA;
GRANT DELETE, INSERT, SELECT, UPDATE ON T_RVW_WP_RESP TO DGS_ICDM_JPA;

------------------------------------------------------------------------------------------------------------------
-- ALM Task ID : 464549
-- Grants of T_COMPLI_RVW_HEX_PARAMS tables to the JPA user
------------------------------------------------------------------------------------------------------------------
GRANT DELETE, INSERT, SELECT, UPDATE ON T_COMPLI_RVW_HEX_PARAMS TO DGS_ICDM_JPA;

 
------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 462621: ACL for services
------------------------------------------------------------------------------------------------------------------
GRANT DELETE, INSERT, SELECT, UPDATE ON T_WS_SERVICES TO DGS_ICDM_JPA;
GRANT DELETE, INSERT, SELECT, UPDATE ON T_WS_SYSTEM_SERVICES TO DGS_ICDM_JPA;

------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 469617: Grant for PK_A2L_WP_DETAILS_COPY package 
------------------------------------------------------------------------------------------------------------------

GRANT EXECUTE ON PK_A2L_WP_DETAILS_COPY TO DGS_ICDM_JPA;

------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 496581: Grant for PK_GROUP2PAL package 
------------------------------------------------------------------------------------------------------------------
GRANT EXECUTE ON PK_GROUP2PAL TO DGS_ICDM_JPA;

 
spool off
