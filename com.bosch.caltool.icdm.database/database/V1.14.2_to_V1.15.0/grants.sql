spool c:\temp\grants.log

--------------------------------------------------------
--  2014-10-15
--------------------------------------------------------

-- ICDM-1026  provide grant in ICDM JPA

GRANT select, insert, update, delete ON T_USECASE_FAVORITES TO DGS_ICDM_JPA;

--------------------------------------------------------
--  2014-10-15
--------------------------------------------------------

-- ICDM-1032  provide grant in ICDM JPA

GRANT select, insert, update, delete ON T_PARAM_ATTRS TO DGS_ICDM_JPA;

spool off
