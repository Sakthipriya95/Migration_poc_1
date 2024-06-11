spool c:\temp\synonyms.log

--------------------------------------------------------
--
-- To be executed in DGS_ICDM_JPA user
--
--------------------------------------------------------


--ICDM-ICDM-2491 create the Synonym for the table in DGS_ICDM 

CREATE OR REPLACE SYNONYM T_RVW_QNAIRE_ANSWER_OPL for DGS_ICDM.T_RVW_QNAIRE_ANSWER_OPL;

CREATE OR REPLACE SYNONYM T_RVW_QNAIRE_RESULTS for DGS_ICDM.T_RVW_QNAIRE_RESULTS;

-- ICDM-2469 create the Synonym for the view in DGS_ICDM 
CREATE OR REPLACE SYNONYM V_VCDM_DATASETS_WORKPKG_STAT FOR DGS_ICDM.V_VCDM_DATASETS_WORKPKG_STAT;

spool off