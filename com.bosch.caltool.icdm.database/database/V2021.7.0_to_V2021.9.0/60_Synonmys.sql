spool c:\temp\60_Synonyms.log


-- ********************************************************
--
-- IMPORTANT : a) To be executed in DGS_ICDM_JPA user
--             b) Set the correct user name
-- ********************************************************


------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 594541: Server side changes - for deletion of A2L related DB entries
------------------------------------------------------------------------------------------------------------------
CREATE OR REPLACE SYNONYM PK_UNMAP_A2L FOR DGS_ICDM.PK_UNMAP_A2L;

------------------------------------------------------------------------------------------------------------------
--  ALM Task : 564953 - Synonyms for table to store unicode remarks for Functions/Rulesets 
------------------------------------------------------------------------------------------------------------------
CREATE OR REPLACE SYNONYM T_RULE_REMARKS FOR DGS_ICDM.T_RULE_REMARKS;

CREATE OR REPLACE SYNONYM GTT_RULE_REMARKS FOR DGS_ICDM.GTT_RULE_REMARKS;

spool off
