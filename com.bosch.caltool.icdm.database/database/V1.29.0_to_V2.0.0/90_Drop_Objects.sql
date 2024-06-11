spool c:\temp\90_Drop_Objects.log
----------------------------------
-- !!! IMPORTANT !!!! - Run this file ONLY if every other file execution is successful
--
-- This file removes the tables, columns and other objects that have 
-- become obsolete after the other changes in database, for this release
----------------------------------

----235846 - drop the column
alter table T_RVW_RESULTS drop column FC2WP_ID;

--235844. Use T_FC2WP_DEFINITION table instead of this
drop table T_FC_GRP_WP_TYPE;

--235845. Use T_FC2WP_DEFINITION table instead of this
drop table T_DIV_FC2WP_TYPE;

spool off