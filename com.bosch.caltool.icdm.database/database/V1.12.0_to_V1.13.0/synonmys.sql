spool c:\temp\synonyms.log

--------------------------------------------------------
--
-- To be executed in DGS_ICDM_JPA user
--
--------------------------------------------------------


--------------------------------------------------------
--  Aug-20-2014
--------------------------------------------------------

--ICDM-954 create the Synonym for the table in DGS_ICDM 

CREATE OR REPLACE SYNONYM T_CHARACTERISTICS for DGS_ICDM.T_CHARACTERISTICS;

CREATE OR REPLACE SYNONYM T_CHARACTERISTIC_VALUES for DGS_ICDM.T_CHARACTERISTIC_VALUES;

CREATE OR REPLACE SYNONYM T_SSD_FEATURES for DGS_ICDM.T_SSD_FEATURES;

CREATE OR REPLACE SYNONYM T_SSD_VALUES for DGS_ICDM.T_SSD_VALUES;


spool off
