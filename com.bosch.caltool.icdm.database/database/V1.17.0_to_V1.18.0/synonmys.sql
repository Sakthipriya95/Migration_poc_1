spool c:\temp\synonyms.log

--------------------------------------------------------
--
-- To be executed in DGS_ICDM_JPA user
--
--------------------------------------------------------


--------------------------------------------------------
--  2015-02-19
--------------------------------------------------------

-- ICDM-1292  create SYNONYM in ICDM JPA
CREATE OR REPLACE SYNONYM T_CP_RULE_ATTRS for DGS_ICDM.T_CP_RULE_ATTRS;

--------------------------------------------------------
--  2015-02-20
--------------------------------------------------------

-- ICDM-1272  create SYNONYM in ICDM JPA

CREATE OR REPLACE SYNONYM T_MESSAGES for DGS_ICDM.T_MESSAGES;

spool off
