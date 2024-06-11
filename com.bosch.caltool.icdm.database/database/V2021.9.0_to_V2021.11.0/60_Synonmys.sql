spool c:\temp\60_Synonyms.log

-- ********************************************************
--
-- IMPORTANT : a) To be executed in DGS_ICDM_JPA user
--             b) Set the correct user name
-- ********************************************************

---------------------------------------------------------------------
--  ALM Task : 618045 - impl : Add context menu option 'Templates for Comments' to Review Results
---------------------------------------------------------------------

CREATE OR REPLACE SYNONYM T_RVW_COMMENT_TEMPLATES FOR DGS_ICDM.T_RVW_COMMENT_TEMPLATES;

---------------------------------------------------------------------

--------------------------------------------------------------------------------------------------------------------------------
--  ALM Task : 618047 - impl : Impl - Add additional context menu option 'Last comments' from which the user can select.
--------------------------------------------------------------------------------------------------------------------------------
--------------------------------------------------------------------------------------------------------------------------------
--ALM Task : 622652 - Impl - Review comment history - Improvements
--------------------------------------------------------------------------------------------------------------------------------


CREATE OR REPLACE SYNONYM T_RVW_USER_CMNT_HISTORY FOR DGS_ICDM.T_RVW_USER_CMNT_HISTORY;

spool off
