spool c:\temp\16.synonyms.log

--------------------------------------------------------
--
-- To be executed in DGS_ICDM_JPA user
--
--------------------------------------------------------

--
-- Create synonyms for T_PIDC_VERSION
--
CREATE OR REPLACE SYNONYM T_PIDC_VERSION for DGS_ICDM.T_PIDC_VERSION;


--
-- Create synonyms for T_PIDC_A2L
--
CREATE OR REPLACE SYNONYM T_PIDC_A2L for DGS_ICDM.T_PIDC_A2L;


spool off
