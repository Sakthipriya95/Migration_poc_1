spool c:\temp\60_Synonyms.log

-- ********************************************************
--
-- IMPORTANT : a) To be executed in DGS_ICDM_JPA user
--             b) Set the correct user name
-- ********************************************************


------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 600278 - Create web service for getting EMR Sheet data
------------------------------------------------------------------------------------------------------------------

--V_PIDC_VARIANTS
CREATE OR REPLACE SYNONYM V_PIDC_VARIANTS FOR DGS_ICDM.V_PIDC_VARIANTS;

--V_EMR_SHEETS_ALL
CREATE OR REPLACE SYNONYM V_EMR_SHEETS_ALL FOR DGS_ICDM.V_EMR_SHEETS_ALL;

--V_EMR_DETAILS_ALL
CREATE OR REPLACE SYNONYM V_EMR_DETAILS_ALL FOR DGS_ICDM.V_EMR_DETAILS_ALL;

spool off
