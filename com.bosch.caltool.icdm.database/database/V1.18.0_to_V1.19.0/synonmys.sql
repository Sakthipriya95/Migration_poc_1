spool c:\temp\synonyms.log

--------------------------------------------------------
--
-- To be executed in DGS_ICDM_JPA user
--
--------------------------------------------------------

--
-- Create synonyms for T_RULE_SET
--
CREATE OR REPLACE SYNONYM T_RULE_SET for DGS_ICDM.T_RULE_SET;
--
-- Create synonyms for T_RULE_SET_PARAMS
--
CREATE OR REPLACE SYNONYM T_RULE_SET_PARAMS for DGS_ICDM.T_RULE_SET_PARAMS;
--
-- Create synonyms for T_RULE_SET_PARAM_ATTR
--
CREATE OR REPLACE SYNONYM T_RULE_SET_PARAM_ATTR for DGS_ICDM.T_RULE_SET_PARAM_ATTR;

spool off
