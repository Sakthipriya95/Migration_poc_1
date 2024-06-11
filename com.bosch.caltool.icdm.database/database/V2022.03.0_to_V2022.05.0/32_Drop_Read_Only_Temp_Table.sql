spool c:\temp\21_Drop_Read_Only_Temp_Table.log

---------------------------------------------------------------------------------------------------------
-- ALM Task : 657723 - impl : Data Migration for Read Only Parameters
--
-- Data Migration to migrate from TA2L_CHARACTERISTICS READONLY column to T_RVW_PARAMETERS READ_ONLY_PARAM
-----------------------------------------------------------------------------------------------------------

DROP TABLE temp_readonly_rvw_param;
    
spool off