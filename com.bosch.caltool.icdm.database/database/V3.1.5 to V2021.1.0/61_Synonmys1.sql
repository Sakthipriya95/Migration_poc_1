spool c:\temp\61_Synonyms.log

-- ********************************************************
--
-- IMPORTANT : a) To be executed in DGS_ICDM_JPA user
--             b) Set the correct user name
-- ********************************************************

------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 553722: DB changes - new table has to be created to store the assessment and 
--result for every individual questions and also data has to be inserted for existing questions
------------------------------------------------------------------------------------------------------------------

CREATE OR REPLACE SYNONYM T_QUESTION_RESULT_OPTIONS FOR DGS_ICDM.T_QUESTION_RESULT_OPTIONS;


------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 555532: DB changes for 100% CDFx delivery documentation
------------------------------------------------------------------------------------------------------------------
CREATE OR REPLACE SYNONYM T_CDFX_DELIVERY FOR DGS_ICDM.T_CDFX_DELIVERY;
CREATE OR REPLACE SYNONYM T_CDFX_DELVRY_WP_RESP FOR DGS_ICDM.T_CDFX_DELVRY_WP_RESP;
CREATE OR REPLACE SYNONYM T_CDFX_DELVRY_PARAM FOR DGS_ICDM.T_CDFX_DELVRY_PARAM;

spool off
