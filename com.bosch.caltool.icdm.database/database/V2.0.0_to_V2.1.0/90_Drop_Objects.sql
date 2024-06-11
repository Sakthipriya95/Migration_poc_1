spool c:\temp\90_Drop_Objects.log

-- 237725: Drop the column WP_GROUP from T_WORKPACKAGE and its references
alter table t_workpackage drop column wp_group;

spool off