spool c:\temp\91_Drop_Objects_JPA.log
----------------------------------
-- !!! IMPORTANT !!!! - Run this file ONLY if every other file execution is successful
--
-- This file removes the tables, columns and other objects that have 
-- become obsolete after the other changes in database, for this release
----------------------------------

--------------------------------------------------------
-- IMPORTANT !!! 
-- To be executed in DGS_ICDM_JPA user
--
--------------------------------------------------------


--235844
drop SYNONYM T_FC_GRP_WP_TYPE;

--235845
drop SYNONYM T_DIV_FC2WP_TYPE;

spool off