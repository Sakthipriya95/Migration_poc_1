spool c:\temp\synonyms.log

--------------------------------------------------------
--
-- To be executed in DGS_ICDM_JPA user
--
--------------------------------------------------------

--------------------------------------------------------
--  2014-11-2
--------------------------------------------------------

--ICDM-2249 create the Synonym for the table in DGS_ICDM 
CREATE OR REPLACE SYNONYM T_FOCUS_MATRIX_REVIEW for DGS_ICDM.T_FOCUS_MATRIX_REVIEW;

--ICDM-2376 create the Synonym for the table in DGS_ICDM 
CREATE OR REPLACE SYNONYM T_WORKPACKAGE for DGS_ICDM.T_WORKPACKAGE;

--ICDM-2376 create the Synonym for the table in DGS_ICDM 
CREATE OR REPLACE SYNONYM T_WORKPACKAGE_DIVISION for DGS_ICDM.T_WORKPACKAGE_DIVISION;

---------------------------------------------------------------
----ICDM-2382
---------------------------------------------------------------
---------------------------------------------------------------
-- T_WS_SYSTEMS grant for web service systems 
---------------------------------------------------------------
CREATE OR REPLACE SYNONYM T_WS_SYSTEMS FOR DGS_ICDM.T_WS_SYSTEMS;

--ICDM-2404
CREATE OR REPLACE SYNONYM T_RVW_QNAIRE_RESPONSE FOR DGS_ICDM.T_RVW_QNAIRE_RESPONSE;


spool off