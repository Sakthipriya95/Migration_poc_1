spool c:\temp\50_Grants.log

---------------------------------------------------------------------------------------------------------
--  ALM Task : 708996 - Provide Grants for Schema DGS_ICDM_JPA to fetch data from the tables T_RULE_LINKS,GTT_RULE_LINKS
---------------------------------------------------------------------------------------------------------

GRANT SELECT, INSERT, UPDATE, DELETE ON T_RULE_LINKS TO DGS_ICDM_JPA;

GRANT SELECT, INSERT, UPDATE, DELETE ON GTT_RULE_LINKS TO DGS_ICDM_JPA;

------------------------------------------------------------------------------------------------------------------------------
---708988: Add New Column in RuleSet Editor
----Provide Grant for tables
------------------------------------------------------------------------------------------------------------------------------

GRANT SELECT, INSERT, UPDATE, DELETE ON T_RULESET_PARAM_TYPE TO DGS_ICDM_JPA;

GRANT SELECT, INSERT, UPDATE, DELETE ON T_RULESET_PARAM_RESP TO DGS_ICDM_JPA;

GRANT SELECT, INSERT, UPDATE, DELETE ON T_RULESET_SYS_ELEMENT TO DGS_ICDM_JPA;

GRANT SELECT, INSERT, UPDATE, DELETE ON T_RULESET_HW_COMPONENT TO DGS_ICDM_JPA;

------------------------------------------------------------------------------------------------------------------
--  ALM Task : 710853 - Provide Grants for Schema DGS_ICDM_JPA to fetch data from the function F_GET_ATTRS_BY_UC_GROUP_IDS
------------------------------------------------------------------------------------------------------------------
GRANT EXECUTE ON F_GET_ATTRS_BY_UC_GROUP_IDS TO DGS_ICDM_JPA;

spool off
