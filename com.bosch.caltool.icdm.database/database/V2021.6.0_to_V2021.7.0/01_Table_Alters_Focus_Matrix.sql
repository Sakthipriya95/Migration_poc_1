spool c:\temp\01_Table_Alters_Focus_Matrix.log

------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 606735: Alter focus matrix table to make is_deleted as not null column
-- Already executed in DEV and PRO environment
------------------------------------------------------------------------------------------------------------------
UPDATE T_FOCUS_MATRIX SET is_deleted='N' where is_deleted is null;
commit;

ALTER TABLE T_FOCUS_MATRIX MODIFY is_deleted varchar2(1) DEFAULT 'N' NOT NULL;
  

spool off
