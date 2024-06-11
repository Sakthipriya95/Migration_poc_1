spool c:\temp\60_synonyms.log

--  ALM TaskId : 263187: Create database scripts for Risk Evaluation
--  Synonyms for Risk Evaluation tables - To be executed in DGS_ICDM_JPA user

CREATE OR REPLACE SYNONYM T_RM_PROJECT_CHARACTER FOR DGS_ICDM.T_RM_PROJECT_CHARACTER;
CREATE OR REPLACE SYNONYM T_RM_RISK_LEVEL FOR DGS_ICDM.T_RM_RISK_LEVEL;
CREATE OR REPLACE SYNONYM T_RM_CATEGORY FOR DGS_ICDM.T_RM_CATEGORY;
CREATE OR REPLACE SYNONYM T_RM_CATEGORY_MEASURES FOR DGS_ICDM.T_RM_CATEGORY_MEASURES;
CREATE OR REPLACE SYNONYM T_RM_CHARACTER_CATEGORY_MATRIX FOR DGS_ICDM.T_RM_CHARACTER_CATEGORY_MATRIX;
CREATE OR REPLACE SYNONYM T_PIDC_RM_DEFINITION FOR DGS_ICDM.T_PIDC_RM_DEFINITION;
CREATE OR REPLACE SYNONYM T_PIDC_RM_PROJECT_CHARACTER FOR DGS_ICDM.T_PIDC_RM_PROJECT_CHARACTER;

spool off