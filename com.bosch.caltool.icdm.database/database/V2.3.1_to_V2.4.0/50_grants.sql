spool c:\temp\50_grants.log

---------------------------------------------------------------
--  ALM TaskId : 333276
--  Grants for table : T_REGION, T_WORKPACKAGE_DIVISION_CDL
---------------------------------------------------------------

GRANT DELETE, INSERT, SELECT, UPDATE ON t_region TO DGS_ICDM_JPA;
GRANT DELETE, INSERT, SELECT, UPDATE ON t_workpackage_division_cdl TO DGS_ICDM_JPA;

---------------------------------------------------------------------
--  ALM Story : 307426
--------------------------------------------------------------------- 
GRANT select, insert, update, delete on T_WEBFLOW_ELEMENT to DGS_ICDM_JPA;

spool off
