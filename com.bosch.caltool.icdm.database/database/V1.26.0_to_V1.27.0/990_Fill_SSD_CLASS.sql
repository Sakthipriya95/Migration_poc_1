spool c:\temp\990_Fill_SSD_CLASS.log

grant UPDATE, SELECT on DGS_ICDM.T_PARAMETER to K5ESK_LDB2 ;

--
-- get the SSD_CLASS from LDB2
--
update t_parameter t1
  set SSD_CLASS = (
                    select nvl(ssd_class, 'NOT_IN_SSD')
                      from t_ldb2_pavast@dgspro.world@k5esk_ldb2
                     where label = t1.name
                  )   
;

--
-- update the SSD_CLASS for parameter not available in LDB2
--
update t_parameter
   set SSD_CLASS = 'NOT_IN_SSD'
 where SSD_CLASS is null
;


--
-- only for testing
--
select ssd_class
     , count(*)
  from T_PARAMETER
 group by SSD_CLASS 
;  

spool off
