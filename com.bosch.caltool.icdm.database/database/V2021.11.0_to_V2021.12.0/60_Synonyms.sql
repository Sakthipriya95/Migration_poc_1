spool c:\temp\60_Synonyms.log


-- ********************************************************
--
-- IMPORTANT : a) To be executed in DGS_ICDM_JPA user
--             b) Set the correct user name
-- ********************************************************


------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 628146: Par2WP: create work packages from functions only if Default WP exists
------------------------------------------------------------------------------------------------------------------
CREATE OR REPLACE SYNONYM PK_CREATE_WP_FROM_FUNC FOR DGS_ICDM.PK_CREATE_WP_FROM_FUNC;


spool off
