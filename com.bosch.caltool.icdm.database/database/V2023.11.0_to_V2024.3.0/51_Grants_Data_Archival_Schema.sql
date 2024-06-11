spool c:\temp\51_Grants_Data_Archival_Schema.log

----------------------------------------------------------
-- IMPORTANT : To be executed in DGS_ICDM_ARCHIVAL schema
----------------------------------------------------------

--------------------------------------------------------------------------------------------------------------------------------
--  ALM Task : 781459 - mpl : WP Archival: Create DB tables and models for WP Archives
--  Grant access for WP Archival tables to other schemes
--  Update access is not given for files table, since updation of Archived Files is not allowed
--------------------------------------------------------------------------------------------------------------------------------

GRANT SELECT, INSERT, UPDATE ON t_wp_archival TO DGS_ICDM_JPA;
GRANT SELECT, INSERT, UPDATE ON t_wp_archival TO DGS_ICDM;

GRANT SELECT, INSERT ON t_wp_files TO DGS_ICDM_JPA;
GRANT SELECT, INSERT ON t_wp_files TO DGS_ICDM;

spool off
