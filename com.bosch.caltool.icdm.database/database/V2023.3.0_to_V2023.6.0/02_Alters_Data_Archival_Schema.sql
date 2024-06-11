spool c:\temp\02_Alters_Data_Archival_Schema.log

----------------------------------------------------------
-- IMPORTANT : To be executed in DGS_ICDM_ARCHIVAL schema
----------------------------------------------------------


--------------------------------------------------------------------------------------------------------------------------------
--  ALM Task : 721705 - Improvement : In PIDC versions page, when the check box 'Consider Reviews of Previous version in reports' is ticked then we could have some indication in Data Assessment Report.
--------------------------------------------------------------------------------------------------------------------------------


-- Adding New Columns 
ALTER TABLE t_da_data_assessment
    ADD previous_pidc_vers_considered VARCHAR2(1 CHAR)          ;

spool off
