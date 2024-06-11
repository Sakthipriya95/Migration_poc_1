spool c:\temp\01_Table_Alters2-NewUniqueConstraint.log

---------------------------------------------------------------------
--  ALM Task Id : 362021 - Remove duplicate records from Secondary Review result parameter table 
--                         Add unique constraint in the table
---------------------------------------------------------------------  

delete FROM t_rvw_parameters_secondary 
WHERE SEC_RVW_PARAM_ID NOT IN 
    (
       SELECT MIN(SEC_RVW_PARAM_ID)
       FROM t_rvw_parameters_secondary
       GROUP BY sec_review_id, rvw_param_id 
    )
commit;

-- Add unique constraint
ALTER TABLE t_rvw_parameters_secondary ADD CONSTRAINT T_RVW_PARAMETERS_SECONDARY_UK UNIQUE (sec_review_id, rvw_param_id)

spool off