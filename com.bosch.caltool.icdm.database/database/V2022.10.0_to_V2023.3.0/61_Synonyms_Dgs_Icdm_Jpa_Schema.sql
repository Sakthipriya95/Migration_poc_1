spool c:\temp\61_Synonyms_Dgs_Icdm_Jpa_Schema.log

---------------------------------------------------------------------  
--------------------------------------------------------------------------------------------------------------------------------
--  ALM Task : 702122 - Imp : DB & Entity, Loader, Command, Basic Service, Service Client and Testcases Creation and to Store the Data Assessment Baseline data.
--------------------------------------------------------------------------------------------------------------------------------

---Create SYNONYM in DGS_ICDM_JPA for archival schema tables

CREATE OR REPLACE SYNONYM t_da_data_assessment FOR DGS_ICDM_ARCHIVAL.t_da_data_assessment; 
CREATE OR REPLACE SYNONYM t_da_files FOR DGS_ICDM_ARCHIVAL.t_da_files; 
CREATE OR REPLACE SYNONYM t_da_parameters FOR DGS_ICDM_ARCHIVAL.t_da_parameters; 
CREATE OR REPLACE SYNONYM t_da_qnaire_resp FOR DGS_ICDM_ARCHIVAL.t_da_qnaire_resp; 
CREATE OR REPLACE SYNONYM t_da_wp_resp FOR DGS_ICDM_ARCHIVAL.t_da_wp_resp; 


---------------------------------------------------------------------------------------------------------
--  ALM Task : 708996 - Create synonymn for the tables T_RULE_LINKS,GTT_RULE_LINKS to fetch data from Schema DGS_ICDM_JPA-
---------------------------------------------------------------------------------------------------------
---Create SYNONYM in DGS_ICDM_JPA for DGS_ICDM RuleLinks tables

CREATE OR REPLACE SYNONYM T_RULE_LINKS FOR DGS_ICDM.T_RULE_LINKS; 

CREATE OR REPLACE SYNONYM GTT_RULE_LINKS FOR DGS_ICDM.GTT_RULE_LINKS;

-----------------------------------------------------------------------------------------------------------------------------
---708988: Add New Column in RuleSet Editor
---Create synonymn for the tables to fetch data from Schema DGS_ICDM_JPA----EXecute from DGS_ICDM_JPA------------
-----------------------------------------------------------------------------------------------------------------------------

CREATE OR REPLACE SYNONYM T_RULESET_PARAM_TYPE FOR DGS_ICDM.T_RULESET_PARAM_TYPE; 

CREATE OR REPLACE SYNONYM T_RULESET_PARAM_RESP FOR DGS_ICDM.T_RULESET_PARAM_RESP; 

CREATE OR REPLACE SYNONYM T_RULESET_SYS_ELEMENT FOR DGS_ICDM.T_RULESET_SYS_ELEMENT;

CREATE OR REPLACE SYNONYM T_RULESET_HW_COMPONENT FOR DGS_ICDM.T_RULESET_HW_COMPONENT; 

---------------------------------------------------------------------------------------------------------
--  ALM Task : 710853 - Create synonymn for the function F_GET_ATTRS_BY_UC_GROUP_IDS to fetch data from Schema DGS_ICDM_JPA-
---------------------------------------------------------------------------------------------------------
---Create SYNONYM in DGS_ICDM_JPA for DGS_ICDM F_GET_ATTRS_BY_UC_GROUP_IDS function

CREATE OR REPLACE SYNONYM DGS_ICDM_JPA.F_GET_ATTRS_BY_UC_GROUP_IDS FOR DGS_ICDM.F_GET_ATTRS_BY_UC_GROUP_IDS;

spool off
