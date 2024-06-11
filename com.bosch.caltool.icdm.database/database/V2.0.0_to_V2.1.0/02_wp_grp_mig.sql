spool c:\temp\02_wp_grp_mig.log

-- 237725: Drop the column WP_GROUP from T_WORKPACKAGE and its references
-- Make wp_name_e unique and drop column wp_group
update t_workpackage
   set wp_name_e = wp_name_e || ' (' || wp_group || ')'
 where wp_id in ( select wp_id from 
                    (
                    Select wp_id, wp_name_e,count(distinct nvl(wp_group,'-')) over (partition by  wp_name_e) no_wp_grps  from t_workpackage
                    ) where no_wp_grps > 1
                );

-- Should return no result                
select wp_name_e, count(nvl(wp_group,'-')) 
  from t_workpackage 
group by wp_name_e having count(nvl(wp_group,'-')) > 1;   

alter table t_workpackage drop constraint T_WORKPACKAGE_UK1;
alter table t_workpackage add constraint T_WORKPACKAGE_UK1 unique (WP_NAME_E);

spool off