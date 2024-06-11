spool c:\temp\synonyms.log

--------------------------------------------------------
--
-- To be executed in DGS_ICDM_JPA user
--
--------------------------------------------------------


--------------------------------------------------------
--  Jul-23-2014
--------------------------------------------------------

--ICDM-870 create the Synonym for the table in DGS_ICDM 

CREATE OR REPLACE SYNONYM GTT_FUNCPARAMS FOR DGS_ICDM.GTT_FUNCPARAMS;


spool off
