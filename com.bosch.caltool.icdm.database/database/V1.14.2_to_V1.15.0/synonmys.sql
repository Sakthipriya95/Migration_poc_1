spool c:\temp\synonyms.log

--------------------------------------------------------
--
-- To be executed in DGS_ICDM_JPA user
--
--------------------------------------------------------


--------------------------------------------------------
--  2014-10-15
--------------------------------------------------------

--ICDM-1026 create the Synonym for the table in DGS_ICDM 

CREATE OR REPLACE SYNONYM T_USECASE_FAVORITES for DGS_ICDM.T_USECASE_FAVORITES;

--------------------------------------------------------
--  2014-10-15
--------------------------------------------------------

--ICDM-1032 create the Synonym for the table in DGS_ICDM 
CREATE OR REPLACE SYNONYM T_PARAM_ATTRS for DGS_ICDM.T_PARAM_ATTRS;

spool off
