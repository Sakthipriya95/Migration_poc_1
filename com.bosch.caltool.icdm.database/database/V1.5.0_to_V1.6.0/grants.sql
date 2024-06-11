spool c:\temp\grants.log

--------------------------------------------------------
--  January-17-2014
--------------------------------------------------------

-- ICDM-518
GRANT select, insert, update, delete ON T_RVW_RESULTS TO DGS_ICDM_JPA;
GRANT select, insert, update, delete ON T_RVW_PARTICIPANTS TO DGS_ICDM_JPA;
GRANT select, insert, update, delete ON T_RVW_FUNCTIONS TO DGS_ICDM_JPA;
GRANT select, insert, update, delete ON T_RVW_PARAMETERS TO DGS_ICDM_JPA;
GRANT select, insert, update, delete ON T_RVW_FILES TO DGS_ICDM_JPA;

--ICDM-513
GRANT select ON T_UNITS TO DGS_ICDM_JPA;

spool off

