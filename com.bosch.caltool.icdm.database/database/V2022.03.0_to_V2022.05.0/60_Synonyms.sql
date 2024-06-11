spool c:\temp\60_Synonyms.log

-- ********************************************************
--
-- IMPORTANT : a) To be executed in DGS_ICDM_JPA user
--             b) Set the correct user name
-- ********************************************************

------------------------------------------------------------------------------------------------------------------
--  ALM Task : 631316 - impl : Store Read-Only and Dependent params in database during review process
------------------------------------------------------------------------------------------------------------------
CREATE OR REPLACE SYNONYM T_A2L_DEP_PARAMS FOR DGS_ICDM.T_A2L_DEP_PARAMS;

spool off
