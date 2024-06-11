spool c:\temp\synonyms.log

--------------------------------------------------------
--  August-15-2013  
--------------------------------------------------------
-- To be executed in DGS_ICDM_JPA user
--------------------------------------------------------

--
-- Create synonyms for Use case mapping tables from DGS_ICDM User
--

CREATE OR REPLACE SYNONYM TABV_USE_CASE_GROUPS FOR DGS_ICDM.TABV_USE_CASE_GROUPS;
CREATE OR REPLACE SYNONYM TABV_USE_CASES FOR DGS_ICDM.TABV_USE_CASES;
CREATE OR REPLACE SYNONYM TABV_USE_CASE_SECTIONS FOR DGS_ICDM.TABV_USE_CASE_SECTIONS;
CREATE OR REPLACE SYNONYM TABV_USE_CASE_POINTS FOR DGS_ICDM.TABV_USE_CASE_POINTS;
CREATE OR REPLACE SYNONYM TABV_UCP_ATTRS FOR DGS_ICDM.TABV_UCP_ATTRS;


spool off