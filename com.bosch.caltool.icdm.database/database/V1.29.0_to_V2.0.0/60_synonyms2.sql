spool c:\temp\60_synonyms.log

--------------------------------------------------------
-- IMPORTANT !!! 
-- To be executed in DGS_ICDM_JPA user
--
--------------------------------------------------------

-------------------------------------------------
--Story 221726
----------------------------------------------------------
CREATE OR REPLACE SYNONYM T_ALTERNATE_ATTR_SYN for DGS_ICDM.T_ALTERNATE_ATTR;

-------------------------------------------------
--Task 231281 Schema for secondary review
-------------------------------------------------

CREATE OR REPLACE SYNONYM T_RVW_RESULTS_SECONDARY for DGS_ICDM.T_RVW_RESULTS_SECONDARY;

CREATE OR REPLACE SYNONYM T_RVW_PARAMETERS_SECONDARY for DGS_ICDM.T_RVW_PARAMETERS_SECONDARY;


spool off