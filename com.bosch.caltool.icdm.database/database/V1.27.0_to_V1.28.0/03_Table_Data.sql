spool c:\temp\03_Table_Data.log


---------------------------------------------------------------
----ICDM-2484
---------------------------------------------------------------

update TABV_COMMON_PARAMS set PARAM_ID='PIDC_DIVISION_ATTR' where PARAM_DESC='Division attribute for PIDC';
commit;

---------------------------------------------------------------
--ICDM-2488
--update SSD compliance rule id and description
---------------------------------------------------------------
  
UPDATE TABV_COMMON_PARAMS SET PARAM_ID = 'SSD_COMPLI_RULE_NODE_ID', PARAM_DESC = 'SSD Node ID for compliance rules'  WHERE PARAM_ID = 'SSD_COMPLI_PARAM_NODE_ID';
--
-- ICDM-2521
--
UPDATE T_QUESTIONNAIRE SET NAME_ENG = NULL, NAME_GER = NULL  WHERE WP_DIV_ID IS NOT NULL;

---------------------------------------------------------------
--ICDM-2408
--Insert new Value in TABV_COMMON_PARAMS for webflow job from client
---------------------------------------------------------------
INSERT INTO TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE,VERSION) 
    values ('WEB_FLOW_JOB_CREATION_ACTIVE','Status of context menu for WebFlow Job in PIDC structure view','Y',1);
    
INSERT INTO TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE,VERSION) 
    values ('WEB_FLOW_JOB_LINK','Link for webflow job','https://rb-webflow.bosch.com/main.aspx?action=CreateJobFromPIDC&pidcId=<elementId>',1);

    
---------------------------------------------------------------
--ICDM-2514
--Migrate positive response from Y to P
---------------------------------------------------------------
update T_RVW_QNAIRE_ANSWER set result='P' where result='Y';

---------------------------------------------------------------
--ICDM-2533
--Insert new Value in TABV_COMMON_PARAMS for ICDM v1.28.0
---------------------------------------------------------------

DELETE FROM TABV_COMMON_PARAMS WHERE PARAM_ID = 'iCDM_CLIENT_VERSION';
INSERT INTO TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE,VERSION) 
    values ('iCDM_CLIENT_VERSION','iCDM Client''s current version','1.28.0',1);
    
commit;

spool off


