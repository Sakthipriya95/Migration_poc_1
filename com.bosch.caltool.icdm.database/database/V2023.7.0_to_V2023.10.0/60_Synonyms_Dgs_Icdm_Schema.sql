spool c:\temp\60_Synonyms_Dgs_Icdm_Schema.log
 
-- ********************************************************
--
-- IMPORTANT : a) To be executed in DGS_ICDM_JPA user
--             b) Set the correct user name
-- ********************************************************

------------------------------------------------------------------------------------------------------------------
--Task 749550: Impl: Azure SSO in iCDM Analysis and implementation Part - 1
------------------------------------------------------------------------------------------------------------------
CREATE OR REPLACE SYNONYM T_USER_LOGIN_INFO FOR DGS_ICDM.T_USER_LOGIN_INFO;

------------------------------------------------------------------------------------------------------------------
--Task 716513: Impl : Number of decimals in check value of review result

------------------------------------------------------------------------------------------------------------------

CREATE OR REPLACE SYNONYM T_USER_PREFERENCES FOR DGS_ICDM.T_USER_PREFERENCES;

spool off
