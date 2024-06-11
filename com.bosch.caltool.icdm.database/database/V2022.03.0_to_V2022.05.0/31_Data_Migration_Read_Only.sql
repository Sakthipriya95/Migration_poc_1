spool c:\temp\21_Data_Migration_Read_Only.log

---------------------------------------------------------------------------------------------------------
-- ALM Task : 657723 - impl : Data Migration for Read Only Parameters
--
-- Data Migration to migrate from TA2L_CHARACTERISTICS READONLY column to T_RVW_PARAMETERS READ_ONLY_PARAM
-----------------------------------------------------------------------------------------------------------
          
--Store read-only parameters in temp table
create table temp_readonly_rvw_param 
as
	select rvw_param.RVW_PARAM_ID 
	from ta2l_characteristics achar
       , ta2l_modules amod   
       , ta2l_fileinfo afi
       , t_parameter param
       , t_rvw_results rvwresult
       , T_PIDC_A2L pidc_A2l
       , t_Rvw_parameters rvw_param
    where amod.file_id = afi.id
	  and achar.module_id = amod.module_id
	  and achar.name = param.name 
	  and achar.dtype = param.ptype
	  and achar.readonly = 1 
	  and rvw_param.param_id = param.id
	  and rvw_param.result_id = rvwresult.RESULT_ID 
	  and rvwresult.PIDC_A2L_ID = pidc_A2l.PIDC_A2L_ID
	  and afi.id = pidc_A2l.A2L_FILE_ID;

UPDATE T_RVW_PARAMETERS SET READ_ONLY_PARAM='Y' 
where RVW_PARAM_ID IN 
     (SELECT RVW_PARAM_ID FROM temp_readonly_rvw_param); 

COMMIT;
    
spool off