spool C:\Temp\90_Drop_Objects.log

-------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 691029 - Impl: Remove 'WP Finished Status' Column/field from A2LWpResponsibility related Objects
-------------------------------------------------------------------------------------------------------------------

ALTER TABLE T_A2L_WP_RESPONSIBILITY DROP COLUMN WP_RESP_STATUS;

spool off
