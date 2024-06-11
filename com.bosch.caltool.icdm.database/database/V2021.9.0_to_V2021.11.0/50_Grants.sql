spool c:\temp\50_Grants.log

---------------------------------------------------------------------
--  ALM Task : 618045 - impl : Add context menu option 'Templates for Comments' to Review Results
---------------------------------------------------------------------

GRANT SELECT, INSERT, UPDATE, DELETE ON T_RVW_COMMENT_TEMPLATES TO DGS_ICDM_JPA;

---------------------------------------------------------------------


--------------------------------------------------------------------------------------------------------------------------------
--  ALM Task : 618047 - impl : Impl - Add additional context menu option 'Last comments' from which the user can select.
--------------------------------------------------------------------------------------------------------------------------------
--------------------------------------------------------------------------------------------------------------------------------
--ALM Task : 622652 - Impl - Review comment history - Improvements
--------------------------------------------------------------------------------------------------------------------------------
 
GRANT SELECT,INSERT,UPDATE,DELETE ON T_RVW_USER_CMNT_HISTORY TO DGS_ICDM_JPA;

spool off
