spool c:\temp\91_Drop_Objects_JPA.log

---------------------------------------------------------------
--ICDM-2561
--drop old synonyms
---------------------------------------------------------------

drop SYNONYM T_FOCUS_MATRIX_REVIEW;

---------------------------------------------------------------
--ICDM-2296
--drop old synonyms
---------------------------------------------------------------
drop SYNONYM T_GROUP_ATTR_VALIDITY;
drop SYNONYM T_GROUP_ATTR_VALUES;

spool off