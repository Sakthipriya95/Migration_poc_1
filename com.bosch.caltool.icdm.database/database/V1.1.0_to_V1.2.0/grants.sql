spool c:\temp\grants.log

--------------------------------------------------------
--  August-15-2013  
--------------------------------------------------------

--
-- Grants for use case mapping tables to DGS_ICDM_JPA user
--

GRANT select, insert, update, delete on TABV_USE_CASE_GROUPS to DGS_ICDM_JPA;
GRANT select, insert, update, delete on TABV_USE_CASES to DGS_ICDM_JPA;
GRANT select, insert, update, delete on TABV_USE_CASE_SECTIONS to DGS_ICDM_JPA;
GRANT select, insert, update, delete on TABV_USE_CASE_POINTS to DGS_ICDM_JPA;
GRANT select, insert, update, delete on TABV_UCP_ATTRS to DGS_ICDM_JPA;


spool off