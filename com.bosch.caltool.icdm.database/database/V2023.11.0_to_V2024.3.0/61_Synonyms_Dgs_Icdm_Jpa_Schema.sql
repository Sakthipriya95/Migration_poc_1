spool c:\temp\61_Synonyms_Dgs_Icdm_Jpa_Schema.log

----------------------------------------------------------
-- IMPORTANT : To be executed in DGS_ICDM_JPA schema
----------------------------------------------------------

--------------------------------------------------------------------------------------------------------------------------------
--  ALM Task : 781459 - mpl : WP Archival: Create DB tables and models for WP Archives
--------------------------------------------------------------------------------------------------------------------------------

---Create SYNONYM in DGS_ICDM_JPA for archival schema tables

CREATE OR REPLACE SYNONYM t_wp_archival FOR DGS_ICDM_ARCHIVAL.t_wp_archival; 
CREATE OR REPLACE SYNONYM t_wp_files FOR DGS_ICDM_ARCHIVAL.t_wp_files;

spool off
