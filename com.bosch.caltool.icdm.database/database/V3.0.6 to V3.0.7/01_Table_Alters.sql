spool c:\temp\01_Table_Alters.log

------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 434834: Add new column to T_RULE_SET TABLE 
--  ALTER query for T_RULE_SET  
------------------------------------------------------------------------------------------------------------------

ALTER TABLE T_RULE_SET ADD (READ_ACCESS_NEEDED_IN_DRT VARCHAR2(1) DEFAULT 'Y' NOT NULL);

spool off