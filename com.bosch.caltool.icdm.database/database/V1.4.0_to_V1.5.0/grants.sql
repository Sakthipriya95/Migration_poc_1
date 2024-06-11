spool c:\temp\grants.log

--------------------------------------------------------
--  November-25-2013  
--------------------------------------------------------

GRANT select, insert, update, delete ON TABV_ICDM_FILES TO DGS_ICDM_JPA;
GRANT select, insert, update, delete ON TABV_ICDM_FILE_DATA TO DGS_ICDM_JPA;

---- iCDM-471 ---
GRANT select ON T_FUNCTIONS TO DGS_ICDM_JPA;
GRANT select ON T_FUNCTIONVERSIONS TO DGS_ICDM_JPA;
GRANT select ON T_UNITS TO DGS_ICDM_JPA;
GRANT select, update ON T_PARAMETER TO DGS_ICDM_JPA;

---- ICDM-497 ---
GRANT select, insert, update, delete ON T_REVIEW_RULES TO DGS_ICDM_JPA;


---- ICDM-470 ---
GRANT DELETE, INSERT, SELECT, UPDATE ON TABV_TOP_LEVEL_ENTITIES TO DGS_ICDM_JPA;

spool off

