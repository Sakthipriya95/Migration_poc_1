spool c:\temp\60_Synonyms_Dgs_Icdm_Schema.log

---------------------------------------------------------------------  
--------------------------------------------------------------------------------------------------------------------------------
--  ALM Task : 702122 - Imp : DB & Entity, Loader, Command, Basic Service, Service Client and Testcases Creation and to Store the Data Assessment Baseline data.
--------------------------------------------------------------------------------------------------------------------------------

---Create SYNONYM in DGS_ICDM for archival schema tables

CREATE OR REPLACE SYNONYM t_da_data_assessment FOR DGS_ICDM_ARCHIVAL.t_da_data_assessment; 
CREATE OR REPLACE SYNONYM t_da_files FOR DGS_ICDM_ARCHIVAL.t_da_files; 
CREATE OR REPLACE SYNONYM t_da_parameters FOR DGS_ICDM_ARCHIVAL.t_da_parameters; 
CREATE OR REPLACE SYNONYM t_da_qnaire_resp FOR DGS_ICDM_ARCHIVAL.t_da_qnaire_resp; 
CREATE OR REPLACE SYNONYM t_da_wp_resp FOR DGS_ICDM_ARCHIVAL.t_da_wp_resp; 

spool off
