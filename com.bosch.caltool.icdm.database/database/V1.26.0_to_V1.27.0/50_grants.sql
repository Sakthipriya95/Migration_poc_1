spool c:\temp\grants.log

--------------------------------------------------------
--  2014-11-2
--------------------------------------------------------

-- ICDM-2249  provide grant in ICDM JPA

GRANT select, insert, update, delete ON T_FOCUS_MATRIX_REVIEW TO DGS_ICDM_JPA;

--ICDM-2376 provide grant in ICDM JPA

GRANT select, insert, update, delete ON T_WORKPACKAGE TO DGS_ICDM_JPA;
  
--ICDM-2376 provide grant in ICDM JPA
GRANT select, insert, update, delete ON T_WORKPACKAGE_DIVISION TO DGS_ICDM_JPA;

---------------------------------------------------------------
----ICDM-2382
---------------------------------------------------------------
---------------------------------------------------------------
-- T_WS_SYSTEMS grant for web service systems 
---------------------------------------------------------------

GRANT select, insert, update, delete ON T_WS_SYSTEMS TO DGS_ICDM_JPA;

--ICDM-2404
GRANT select, insert, update, delete ON T_RVW_QNAIRE_RESPONSE TO DGS_ICDM_JPA;

spool off
