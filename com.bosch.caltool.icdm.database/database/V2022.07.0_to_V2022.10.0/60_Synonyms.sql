spool c:\temp\60_Synonyms.log

-- ********************************************************
--
-- IMPORTANT : a) To be executed in DGS_ICDM_JPA user
--             b) Set the correct user name
-- ********************************************************

------------------------------------------------------------------------------------------------------------------
--  ALM Task : 664472 - impl : Par2WP Mappings are modified
------------------------------------------------------------------------------------------------------------------
CREATE OR REPLACE SYNONYM PRC_UPDATE_A2L_FINISHED FOR DGS_ICDM.PRC_UPDATE_A2L_FINISHED;

-------------------------------------------------------------------------------------------------------------------
--ALM Task :689706 - Impl : Server side Changes –  Design new table for storing A2l_WP_RESP_Finished status
--------------------------------------------------------------------------------------------------------------------
CREATE OR REPLACE SYNONYM T_A2L_WP_RESPONSIBILITY_STATUS FOR DGS_ICDM.T_A2L_WP_RESPONSIBILITY_STATUS; 


spool off
