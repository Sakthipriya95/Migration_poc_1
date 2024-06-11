spool c:\temp\50_Grants.log

------------------------------------------------------------------------------------------------------------------
--  ALM Task : 631316 - impl : Store Read-Only and Dependent params in database during review process
------------------------------------------------------------------------------------------------------------------
GRANT SELECT, INSERT, UPDATE, DELETE ON T_A2L_DEP_PARAMS TO DGS_ICDM_JPA;

spool off
