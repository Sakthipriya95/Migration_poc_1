spool c:\temp\table_updates.log

--------------------------------------------------------
--  July-07-2014
-------------------------------------------------------- 

------------------------------------------
-- New column REVIEW_TYPE in T_RVW_RESULTS table. Possible values T-Test, O-Official
-- ICDM-873
------------------------------------------
ALTER TABLE T_RVW_RESULTS ADD (REVIEW_TYPE VARCHAR2(1))
/

UPDATE T_RVW_RESULTS SET REVIEW_TYPE = 'T'
/

COMMIT
/

ALTER TABLE T_RVW_RESULTS MODIFY (REVIEW_TYPE VARCHAR2(1) NOT NULL)
/

------------------------------------------
-- New table GTT_FUNCPARAMS for storing the Function names  param names and param type
-- ICDM-870
------------------------------------------
CREATE GLOBAL TEMPORARY TABLE GTT_FUNCPARAMS
(
  ID          NUMBER PRIMARY KEY,
  PARAM_NAME  VARCHAR2(255 BYTE),
  FUN_NAME    VARCHAR2(255 BYTE),
  TYPE        VARCHAR2(30 BYTE)
);

--------------------------------------------------------
--  Version update script
--------------------------------------------------------      
delete from TABV_COMMON_PARAMS WHERE PARAM_ID = 'iCDM_CLIENT_VERSION';
Insert into TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE,VERSION) values ('iCDM_CLIENT_VERSION','iCDM Client''s current version','1.12.0',1);

spool off