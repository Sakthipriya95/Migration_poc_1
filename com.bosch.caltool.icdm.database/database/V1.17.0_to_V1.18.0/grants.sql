spool c:\temp\grants.log

--------------------------------------------------------
--  2015-02-19
--------------------------------------------------------

-- ICDM-1292  provide grant in ICDM JPA

GRANT select, insert, update, delete ON T_CP_RULE_ATTRS TO DGS_ICDM_JPA;

--------------------------------------------------------
--  2015-02-20
--------------------------------------------------------

-- ICDM-1272  provide grant in ICDM JPA

GRANT select, insert, update, delete ON T_MESSAGES TO DGS_ICDM_JPA;

spool off
