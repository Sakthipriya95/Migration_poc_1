spool c:\temp\51_Grants_Qnaire.log


------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 567145: Create tables and Constrains based on ER diagram for questionnaire enhancements
------------------------------------------------------------------------------------------------------------------

--t_rvw_qnaire_resp_versions
GRANT DELETE, INSERT, SELECT, UPDATE ON t_rvw_qnaire_resp_versions TO DGS_ICDM_JPA;

--t_rvw_qnaire_resp_variants
GRANT DELETE, INSERT, SELECT, UPDATE ON t_rvw_qnaire_resp_variants TO DGS_ICDM_JPA;


spool off