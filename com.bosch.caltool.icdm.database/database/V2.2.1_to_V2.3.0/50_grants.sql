spool c:\temp\50_grants.log

-------------------------------------------------------------
------ 275697 : CODEX : Emission Robustness related grants
-------------------------------------------------------------

GRANT DELETE, INSERT, SELECT, UPDATE ON t_emr_category TO DGS_ICDM_JPA;
GRANT DELETE, INSERT, SELECT, UPDATE ON t_emr_measure_unit TO DGS_ICDM_JPA;
GRANT DELETE, INSERT, SELECT, UPDATE ON t_emr_column TO DGS_ICDM_JPA;
GRANT DELETE, INSERT, SELECT, UPDATE ON t_emr_column_value TO DGS_ICDM_JPA;
GRANT DELETE, INSERT, SELECT, UPDATE ON t_emr_emission_standard TO DGS_ICDM_JPA;
GRANT DELETE, INSERT, SELECT, UPDATE ON t_emr_excel_mapping TO DGS_ICDM_JPA;
GRANT DELETE, INSERT, SELECT, UPDATE ON t_emr_file TO DGS_ICDM_JPA;
GRANT DELETE, INSERT, SELECT, UPDATE ON t_emr_upload_error TO DGS_ICDM_JPA;
GRANT DELETE, INSERT, SELECT, UPDATE ON t_emr_pidc_variant TO DGS_ICDM_JPA;
GRANT DELETE, INSERT, SELECT, UPDATE ON t_emr_file_data TO DGS_ICDM_JPA;

---------------------------------------------------------------------
--  ALM TaskId : 279252: Get BC info using GET_PVER_BC function
---------------------------------------------------------------------
GRANT INSERT, SELECT ON GTT_EASEE_ELEMENTS to DGS_ICDM_JPA;
GRANT EXECUTE ON GET_PVER_BC TO DGS_ICDM_JPA;


--------------------------------------------------------------
------ Task 281621 : Tables to store COMPLI Check WebService results 
-------------------------------------------------------------  
GRANT DELETE, INSERT, SELECT, UPDATE ON T_COMPLI_RVW_A2L TO DGS_ICDM_JPA;
GRANT DELETE, INSERT, SELECT, UPDATE ON T_COMPLI_RVW_HEX TO DGS_ICDM_JPA;

spool off
