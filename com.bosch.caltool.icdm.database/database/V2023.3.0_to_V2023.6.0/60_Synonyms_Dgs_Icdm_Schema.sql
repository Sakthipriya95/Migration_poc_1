spool c:\temp\60_Synonyms_Dgs_Icdm_Schema.log
 
-- ********************************************************
--
-- IMPORTANT : a) To be executed in DGS_ICDM_JPA user
--             b) Set the correct user name
-- ********************************************************
------------------------------------------------------------------------------------------------------------------
--Task 729577 :Allow adding groups to access rights page of PIDCs

------------------------------------------------------------------------------------------------------------------
CREATE OR REPLACE SYNONYM "T_ACTIVE_DIRECTORY_GROUPS" FOR "DGS_ICDM"."T_ACTIVE_DIRECTORY_GROUPS";

CREATE OR REPLACE SYNONYM "T_ACTIVE_DIRECTORY_GROUP_USERS" FOR "DGS_ICDM"."T_ACTIVE_DIRECTORY_GROUP_USERS";

CREATE OR REPLACE SYNONYM "T_ACTIVE_DIRECTORY_GROUP_NODE_ACCESS" FOR "DGS_ICDM"."T_ACTIVE_DIRECTORY_GROUP_NODE_ACCESS";


spool off
