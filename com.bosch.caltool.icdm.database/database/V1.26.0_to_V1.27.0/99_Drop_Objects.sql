spool c:\temp\99_Drop_Objects.log
----------------------------------
-- !!! IMPORTANT !!!! - Run this file ONLY if every other file execution is successful
--
-- This file removes the tables, columns and other objects that have 
-- become obsolete after the other changes in database, for this release
----------------------------------


--------------------------------------------
--ICDM-2357
--------------------------------------------
-- drop the temp_open_points column from table T_RVW_QNAIRE_ANSWER
ALTER TABLE T_RVW_QNAIRE_ANSWER DROP COLUMN TEMP_OPEN_POINTS;

--------------------------------------------
--ICDM-2404
--------------------------------------------
ALTER TABLE T_RVW_QNAIRE_ANSWER DROP COLUMN VARIANT_ID;
ALTER TABLE T_RVW_QNAIRE_ANSWER DROP COLUMN PIDC_VERS_ID;
ALTER TABLE T_RVW_QNAIRE_ANSWER DROP COLUMN QNAIRE_VERS_ID;

ALTER TABLE T_RVW_QUESTIONNAIRE DROP COLUMN QNAIRE_VERS_ID;


spool off