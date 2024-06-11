spool c:\temp\50_Grants.log

------------------------------------------------------------------------------------------------------------------
--Task 749550: Impl: Azure SSO in iCDM Analysis and implementation Part - 1
------------------------------------------------------------------------------------------------------------------
GRANT DELETE ON T_USER_LOGIN_INFO TO DGS_ICDM_JPA;
GRANT INSERT ON T_USER_LOGIN_INFO TO DGS_ICDM_JPA;
GRANT SELECT ON T_USER_LOGIN_INFO TO DGS_ICDM_JPA;
GRANT UPDATE ON T_USER_LOGIN_INFO TO DGS_ICDM_JPA;

------------------------------------------------------------------------------------------------------------------
--Task 716513: Impl : Number of decimals in check value of review result
------------------------------------------------------------------------------------------------------------------

GRANT SELECT, INSERT, UPDATE, DELETE ON T_USER_PREFERENCES TO DGS_ICDM_JPA;

spool off
