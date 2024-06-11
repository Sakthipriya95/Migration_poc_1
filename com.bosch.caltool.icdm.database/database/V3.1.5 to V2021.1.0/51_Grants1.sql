spool c:\temp\51_Grants.log


-- ********************************************************
--
-- IMPORTANT : a) Set the correct user name
--
-- ********************************************************


------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 553722: DB changes - new table has to be created to store the assessment and 
--result for every individual questions and also data has to be inserted for existing questions
------------------------------------------------------------------------------------------------------------------

GRANT DELETE, INSERT, SELECT, UPDATE ON T_QUESTION_RESULT_OPTIONS TO DGS_ICDM_JPA;

------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 555532: DB changes for 100% CDFx delivery documentation
------------------------------------------------------------------------------------------------------------------
GRANT SELECT, INSERT, UPDATE, DELETE ON T_CDFX_DELIVERY TO DGS_ICDM_JPA;
GRANT SELECT, INSERT, UPDATE, DELETE ON T_CDFX_DELVRY_WP_RESP TO DGS_ICDM_JPA;
GRANT SELECT, INSERT, UPDATE, DELETE ON T_CDFX_DELVRY_PARAM TO DGS_ICDM_JPA;

spool off
