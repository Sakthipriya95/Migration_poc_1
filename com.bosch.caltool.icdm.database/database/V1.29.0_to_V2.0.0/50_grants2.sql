spool c:\temp\50_grants.log

-------------------------------------------------
--Story 221726
----------------------------------------------------------
GRANT DELETE, INSERT, SELECT, UPDATE ON T_ALTERNATE_ATTR TO DGS_ICDM_JPA


-------------------------------------------------
--Task 231281 Schema for secondary review
-------------------------------------------------

GRANT select, insert, update, delete on T_RVW_RESULTS_SECONDARY to DGS_ICDM_JPA;

GRANT select, insert, update, delete on T_RVW_PARAMETERS_SECONDARY to DGS_ICDM_JPA;


spool off