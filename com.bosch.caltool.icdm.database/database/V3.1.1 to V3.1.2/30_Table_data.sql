spool c:\temp\30_Table_data.log

------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 510469: Create folder which contain all input file for doing compli review - Server side changes 
--  Insert query for T_MESSAGES
------------------------------------------------------------------------------------------------------------------
Insert into T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER) values ('COMPLI_REVIEW','INPUT_FOLDER_FAILED','Failed to generate compliance review input data folder',null);
COMMIT;

------------------------------------------------------------------------------------------------------------------
-- ALM TaskId : 514696 : Version update script
------------------------------------------------------------------------------------------------------------------      
delete from TABV_COMMON_PARAMS WHERE upper(PARAM_ID) = 'ICDM_CLIENT_VERSION';
Insert into TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE,VERSION) values ('ICDM_CLIENT_VERSION','iCDM Client''s current version','3.1.2',1); 
COMMIT;

------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 518640: Error msg to be shown during import FC2WP when config attr is not set in pidc 
--  Insert query for T_MESSAGES
------------------------------------------------------------------------------------------------------------------
Insert into T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER) values ('FC2WP','DIV_ATTR_NOT_SET','FC2WP mapping incomplete for the PIDC. Please assign value for the following attribute in your PIDC : {0}. ',null);
COMMIT;
spool off

