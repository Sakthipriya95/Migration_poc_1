spool c:\temp\grants.log

--------------------------------------------------------
--  2014-12-30
--------------------------------------------------------

-- ICDM-1214  provide grant in ICDM JPA

GRANT select, insert, update, delete ON T_RVW_ATTR_VALUES TO DGS_ICDM_JPA;


-- ICDM-1179  provide grant in ICDM JPA

GRANT select, insert, update, delete ON T_FC_GRP_WP_TYPE TO DGS_ICDM_JPA;



spool off
