spool c:\temp\61_Synonyms_Qnaire.log

-- ********************************************************
--
-- IMPORTANT : a) To be executed in DGS_ICDM_JPA user
--             b) Set the correct user name
-- ********************************************************



------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 567145: Create tables and Constrains based on ER diagram for questionnaire enhancements
------------------------------------------------------------------------------------------------------------------

--t_rvw_qnaire_resp_versions
CREATE OR REPLACE SYNONYM t_rvw_qnaire_resp_versions FOR DGS_ICDM.t_rvw_qnaire_resp_versions;

--t_rvw_qnaire_resp_variants
CREATE OR REPLACE SYNONYM t_rvw_qnaire_resp_variants FOR DGS_ICDM.t_rvw_qnaire_resp_variants;

spool off
