spool c:\temp\grants.log

--------------------------------------------------------
--  Jun-13-2014
--------------------------------------------------------

-- ICDM-801  provide grant in ICDM JPA
GRANT DELETE, INSERT, SELECT, UPDATE ON GTT_OBJECT_NAMES TO DGS_ICDM_JPA;


--------------------------------------------------------
--  June-16-2014
--------------------------------------------------------

-- iCDM-817

GRANT select, insert, update, delete on T_COMP_PKG to DGS_ICDM_JPA;

GRANT select, insert, update, delete on T_COMP_PKG_BC to DGS_ICDM_JPA;

GRANT select, insert, update, delete on T_COMP_PKG_BC_FC to DGS_ICDM_JPA;


spool off
