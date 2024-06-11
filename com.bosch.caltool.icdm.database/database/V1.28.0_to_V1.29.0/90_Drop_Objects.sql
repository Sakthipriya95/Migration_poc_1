spool c:\temp\90_Drop_Objects.log
----------------------------------
-- !!! IMPORTANT !!!! - Run this file ONLY if every other file execution is successful
--
-- This file removes the tables, columns and other objects that have 
-- become obsolete after the other changes in database, for this release
----------------------------------

---------------------------------------------------------------
----ICDM-2561 - focus matrix review Database scripts
---------------------------------------------------------------

drop table T_FOCUS_MATRIX_REVIEW cascade constraints;

alter table T_FOCUS_MATRIX drop column PIDC_VERS_ID;


---------------------------------------------------------------
--ICDM-2296 - drop old triggers
---------------------------------------------------------------


drop trigger TRG_GROUP_ATTR_VAL_INS;
drop trigger TRG_GROUP_ATTR_VAL_UPD;
drop trigger TRG_GROUP_ATTR_VALIDITY_INS;
drop trigger TRG_GROUP_ATTR_VALIDITY_UPD;


spool off