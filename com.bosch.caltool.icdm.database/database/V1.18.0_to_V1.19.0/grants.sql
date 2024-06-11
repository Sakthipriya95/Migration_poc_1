spool c:\temp\grants.log

--
-- Grants for ruleset table to DGS_ICDM_JPA user
--

GRANT select, insert, update, delete ON T_RULE_SET TO DGS_ICDM_JPA;

--
-- Grants for ruleset param table to DGS_ICDM_JPA user
--

GRANT select, insert, update, delete ON T_RULE_SET_PARAMS TO DGS_ICDM_JPA;

--
-- Grants for ruleset param attr table to DGS_ICDM_JPA user
--

GRANT select, insert, update, delete ON T_RULE_SET_PARAM_ATTR TO DGS_ICDM_JPA;

spool off
