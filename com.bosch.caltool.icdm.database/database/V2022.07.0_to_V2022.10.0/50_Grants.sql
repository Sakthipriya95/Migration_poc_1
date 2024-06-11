spool c:\temp\50_Grants.log

------------------------------------------------------------------------------------------------------------------
--  ALM Task : 664472 - impl : Par2WP Mappings are modified
------------------------------------------------------------------------------------------------------------------
GRANT EXECUTE ON PRC_UPDATE_A2L_FINISHED TO DGS_ICDM_JPA;

-------------------------------------------------------------------------------------------------------------------
--ALM Task :689706 - Impl : Server side Changes –  Design new table for storing A2l_WP_RESP_Finished status
--------------------------------------------------------------------------------------------------------------------
GRANT SELECT, INSERT, UPDATE, DELETE ON T_A2L_WP_RESPONSIBILITY_STATUS TO DGS_ICDM_JPA;

spool off