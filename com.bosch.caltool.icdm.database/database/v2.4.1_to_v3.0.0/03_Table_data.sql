spool c:\temp\03_Table_data.log

-- **************************************************************
-- IMPORTANT : before executing the script, set the <ENV-CODE> to appropriate value : PRO / BETA_TEST / DEV_1 etc.
-- **************************************************************



--------------------------------------------------------
--  343772 Version update script
--------------------------------------------------------      
delete from TABV_COMMON_PARAMS where PARAM_ID = 'iCDM_CLIENT_VERSION';
insert into TABV_COMMON_PARAMS 
    (PARAM_ID,PARAM_DESC,PARAM_VALUE,VERSION) 
  values ('iCDM_CLIENT_VERSION','iCDM Client''s current version','3.0.0',1);

--------------------------------------------------------
--  324754 URL to CNS Server
--------------------------------------------------------      
insert into TABV_COMMON_PARAMS 
    (PARAM_ID,PARAM_DESC,PARAM_VALUE,VERSION) 
  values ('CNS_SERVER_URL','Base URL of CNS Server','http://localhost:8580/com.bosch.caltool.icdm.cns.server/services',1);
  
--------------------------------------------------------
--  338716 ICDM Environment Code
--------------------------------------------------------      
insert into TABV_COMMON_PARAMS 
    (PARAM_ID,PARAM_DESC,PARAM_VALUE,VERSION) 
  values ('ICDM_ENV_CODE', 'iCDM Environment Code', <ENV-CODE>, 1);
  
--------------------------------------------------------    
--  Insert into T_MESSAGES table
--------------------------------------------------------   
insert into T_MESSAGES(GROUP_NAME,NAME,MESSAGE_TEXT) values ('PIDC_EDITOR','GROUPED_ATTR','This is a grouped attribute.');



COMMIT;

spool off