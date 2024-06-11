spool c:\temp\51_Grants_Data_Archival_Schema.log

----------------------------------------------------------
-- IMPORTANT : To be executed in DGS_ICDM_ARCHIVAL schema
----------------------------------------------------------



--------------------------------------------------------------------------------------------------------------
-- ALM Task 702122 : Imp : DB & Entity, Loader, Command, Basic Service, Service Client and Testcases Creation 
--     and to store the Data Assessment Baseline data.

-- ALM Task :  718916: Impl: Performance improvement for the creation of compare HEX pdf report
-- Grant update to T_da_dataassessment table to update file archival status based on the status of baseline file creation
--------------------------------------------------------------------------------------------------------------

GRANT SELECT, INSERT, UPDATE ON t_da_data_assessment TO DGS_ICDM_JPA;
GRANT SELECT, INSERT, UPDATE ON t_da_data_assessment TO DGS_ICDM;

GRANT SELECT, INSERT ON t_da_files TO DGS_ICDM_JPA;
GRANT SELECT, INSERT ON t_da_files TO DGS_ICDM;

GRANT SELECT, INSERT ON t_da_parameters TO DGS_ICDM_JPA;
GRANT SELECT, INSERT ON t_da_parameters TO DGS_ICDM;

GRANT SELECT, INSERT ON t_da_qnaire_resp TO DGS_ICDM_JPA;
GRANT SELECT, INSERT ON t_da_qnaire_resp TO DGS_ICDM;

GRANT SELECT, INSERT ON t_da_wp_resp TO DGS_ICDM_JPA;
GRANT SELECT, INSERT ON t_da_wp_resp TO DGS_ICDM;

spool off
