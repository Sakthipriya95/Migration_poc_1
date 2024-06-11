spool c:\temp\05_grants.log


--------------------------------------------------------
--  ICDM-2189  provide grant in ICDM JPA
--------------------------------------------------------

GRANT select, insert, update, delete ON T_QNAIRE_ANS_OPEN_POINTS TO DGS_ICDM_JPA;


--------------------------------------------------------
--  ICDM-2295  provide grant in ICDM JPA
--------------------------------------------------------

GRANT DELETE, INSERT, SELECT, UPDATE ON T_GROUP_ATTR_VALUES TO DGS_ICDM_JPA;
  
GRANT DELETE, INSERT, SELECT, UPDATE ON T_GROUP_ATTR_VALIDITY TO DGS_ICDM_JPA;

spool off