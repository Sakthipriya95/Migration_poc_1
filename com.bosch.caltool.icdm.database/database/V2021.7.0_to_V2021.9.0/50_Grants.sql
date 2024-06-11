spool c:\temp\50_Grants.log


-- ********************************************************
--
-- IMPORTANT : a) Set the correct user name
--
-- ********************************************************



------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 594541: Server side changes - for deletion of A2L related DB entries
------------------------------------------------------------------------------------------------------------------
GRANT EXECUTE ON PK_UNMAP_A2L TO DGS_ICDM_JPA;

------------------------------------------------------------------------------------------------------------------
--  ALM Task : 564953 - Grant for table to store unicode remarks for Functions/Rulesets 
------------------------------------------------------------------------------------------------------------------
GRANT SELECT, INSERT, UPDATE, DELETE ON T_RULE_REMARKS TO DGS_ICDM_JPA;

GRANT SELECT, INSERT, UPDATE, DELETE ON GTT_RULE_REMARKS TO DGS_ICDM_JPA;

------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 611788: Impl: Review Rule Editor: Field 'Unit' should be editable
------------------------------------------------------------------------------------------------------------------
GRANT INSERT ON T_UNITS TO DGS_ICDM_JPA;

spool off
