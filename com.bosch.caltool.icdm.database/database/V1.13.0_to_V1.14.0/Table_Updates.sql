spool c:\temp\table_updates.log

--------------------------------------------------------
--  September-19-2014
-------------------------------------------------------- 

------------------------------------------
-- varchar size is increased to 15 bytes for NODE_TYPE column in T_LINKS table
-- ICDM-977
------------------------------------------

alter table T_LINKS modify  NODE_TYPE varchar2(15);

------------------------------------------
-- NODE TYPE strings changed for NODE_TYPE column in T_LINKS table
-- ICDM-977
------------------------------------------

UPDATE T_LINKS SET NODE_TYPE = 'APIC_ATTR' WHERE NODE_TYPE = 'ATTR';

UPDATE T_LINKS SET NODE_TYPE = 'USECASE' WHERE NODE_TYPE = 'UC' ;

UPDATE T_LINKS SET NODE_TYPE = 'USE_CASE_SECT' WHERE NODE_TYPE = 'UCS' ;

UPDATE T_LINKS SET NODE_TYPE='SUPER_GROUP' WHERE NODE_TYPE='SGRP';

UPDATE T_LINKS SET NODE_TYPE='GROUP' WHERE NODE_TYPE='GRP';

COMMIT;

-----------------------------------------------------------------------------
-- ICDM-999
-- Removes the value column from the project attributes tables
-----------------------------------------------------------------------------

--Recreate unique constraint TABV_PROJECT_ATTR_UNI
ALTER TABLE  TABV_PROJECT_ATTR DROP CONSTRAINT TABV_PROJECT_ATTR_UNI;
DROP INDEX TABV_PROJECT_ATTR_UNI;
ALTER TABLE  TABV_PROJECT_ATTR ADD CONSTRAINT TABV_PROJECT_ATTR_UNI UNIQUE ("PROJECT_ID", "PRO_REV_ID", "ATTR_ID");

--Recreate unique constraint TABV_VARIANTS_ATTR_UNI
ALTER TABLE  TABV_VARIANTS_ATTR DROP CONSTRAINT TABV_VARIANTS_ATTR_UNI;
DROP INDEX TABV_VARIANTS_ATTR_UNI;
ALTER TABLE  TABV_VARIANTS_ATTR ADD  CONSTRAINT TABV_VARIANTS_ATTR_UNI UNIQUE ("VARIANT_ID", "PRO_REV_ID", "ATTR_ID");

--Recreate unique constraint TABV_PROJ_SV_ATTR_UNI
ALTER TABLE  TABV_PROJ_SUB_VARIANTS_ATTR DROP CONSTRAINT TABV_PROJ_SV_ATTR_UNI;
DROP INDEX TABV_PROJ_SV_ATTR_I;
ALTER TABLE  TABV_PROJ_SUB_VARIANTS_ATTR ADD  CONSTRAINT TABV_PROJ_SV_ATTR_UNI UNIQUE ("SUB_VARIANT_ID", "PRO_REV_ID", "ATTR_ID");

---------------------------------------------------------------------------------------------

delete from TABV_COMMON_PARAMS WHERE PARAM_ID = 'iCDM_CLIENT_VERSION';
Insert into TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE,VERSION) values ('iCDM_CLIENT_VERSION','iCDM Client''s current version','1.14.0',1);
COMMIT;


spool off

