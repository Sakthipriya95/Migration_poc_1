spool c:\temp\99_Drop_Objects.log

--------------------------------------------
--ICDM-2189
--------------------------------------------
-- drop the open_points column from table T_RVW_QNAIRE_ANSWER
ALTER TABLE T_RVW_QNAIRE_ANSWER DROP COLUMN OPEN_POINTS;


--------------------------------------------
--ICDM-2305
--------------------------------------------
-- drop the column REVIEWED_FLAG from T_RVW_PARAMETERS
ALTER TABLE t_rvw_parameters DROP COLUMN REVIEWED_FLAG;



spool off