spool c:\temp\30_Table_Data.log

-----------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 518935: db task- New column in wp definition version to check if it was active anytime
------------------------------------------------------------------------------------------------------------------
--  update ANYTIME_ACTIVE_FLAG for existing records
UPDATE T_A2L_WP_DEFN_VERSIONS VERS SET ANYTIME_ACTIVE_FLAG = 'Y' WHERE IS_ACTIVE = 'Y';
UPDATE T_A2L_WP_DEFN_VERSIONS VERS SET ANYTIME_ACTIVE_FLAG = 'Y' 
    WHERE WP_DEFN_VERS_ID IN 
        (SELECT WP_DEFN_VERS_ID FROM T_RVW_RESULTS WHERE WP_DEFN_VERS_ID IS NOT NULL);

COMMIT;

------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 515500: Impl : Import Button in Compli review dialog to import the input data using execution id 
--  Insert query for T_MESSAGES
------------------------------------------------------------------------------------------------------------------
Insert into T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER) values ('COMPLI_REVIEW','SERVER_PATH_MISSING','Server path not available in database. Please get in contact with iCDM hotline',null);
Insert into T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER) values ('COMPLI_REVIEW','FILE_DOWNLOAD_ERROR','Error downloading files for execution ID : {0}',null);
Insert into T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER) values ('COMPLI_REVIEW','FILES_NOT_AVAILABLE','Files not available for execution ID : {0}',null);
COMMIT;


------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 515500: Impl : Import Button in Compli review dialog to import the input data using execution id 
--  Insert query for TABV_COMMON_PARAMS
------------------------------------------------------------------------------------------------------------------
Insert into TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE) values ('SERVER_GROUP_WORK_PATHS','List of work directories of all servers in this server group(DEV/BETA/PRO etc.). Multiple paths are separated by semicolon(;)','\\si-cdm02.de.bosch.com\iCDM_WebService\ID_07\work');
COMMIT;

------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 515911: Insert values into T_MESSAGES table - new error message message for editing sdom pver attribute   
------------------------------------------------------------------------------------------------------------------
INSERT INTO T_MESSAGES(GROUP_NAME, NAME, MESSAGE_TEXT) VALUES  ('PIDC_EDITOR','A2L_MAP_ERROR','To modify PVER value, Please un-assign a2l file(s) from the current PIDC version.  In case of issues, please contact iCDM Hotline');
COMMIT;

------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 521869: Warning message while entering invalid characters for A2LWorkpackage or A2LResponsibility name    
------------------------------------------------------------------------------------------------------------------
INSERT INTO T_MESSAGES(GROUP_NAME, NAME, MESSAGE_TEXT) VALUES  ('A2L','INVALID_WP_RESP_ERROR','Allowed characters are alphanumeric, underscore and square brackets');
COMMIT;

------------------------------------------------------------------------------------------------------------------
-- ALM TaskId : 524922 : Version update script
------------------------------------------------------------------------------------------------------------------      
delete from TABV_COMMON_PARAMS WHERE upper(PARAM_ID) = 'ICDM_CLIENT_VERSION';
Insert into TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE,VERSION) values ('ICDM_CLIENT_VERSION','iCDM Client''s current version','3.1.4',1); 
COMMIT;
------------------------------------------------------------------------------------------------------------------
-- ALM TaskId : 529382 : New error message for DB Exceptions when fetching Bc's 
------------------------------------------------------------------------------------------------------------------    
Insert into T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT) values ('BASE_COMP','BC_FETCH_ERROR_DB','Error while fetching BC mappings : {0}');
COMMIT;

------------------------------------------------------------------------------------------------------------------
-- ALM TaskId : 526582 : Update maturity level in T_RVW_PARAMETERS from SSD view V_LDB2_MATURITY_LEVEL
------------------------------------------------------------------------------------------------------------------     
UPDATE T_RVW_PARAMETERS rvw_param 
SET rvw_param.MATURITY_LEVEL= 
  (SELECT MATURITY FROM V_LDB2_MATURITY_LEVEL ssd_maturity
  WHERE ssd_maturity.LAB_OBJ_ID= rvw_param.LAB_OBJ_ID AND ssd_maturity.REV_ID=rvw_param.REV_ID)
WHERE EXISTS (
    SELECT 1
      FROM V_LDB2_MATURITY_LEVEL ssd_maturity
     WHERE ssd_maturity.LAB_OBJ_ID= rvw_param.LAB_OBJ_ID AND ssd_maturity.REV_ID=rvw_param.REV_ID);  
COMMIT;

------------------------------------------------------------------------------------------------------------------
-- ALM TaskId : 529723 : Dev - New COMMON_PARM configuration to store the ‘to’ email IDs for compliance issue reporting.
------------------------------------------------------------------------------------------------------------------
INSERT INTO TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE) VALUES
('COMPLI_ISSUE_REPORT_TO','To address of users seperated by ";" for sending Compliance review issue mail','iCDM.Hotline-Clearing@de.bosch.com');
COMMIT;

------------------------------------------------------------------------------------------------------------------
-- ALM TaskId : 529726 : Dev - Define a new access right, so that user with that access can also use the import JSON feature
------------------------------------------------------------------------------------------------------------------

--Insert query for enabling CMPLI_RVW_READ node access for Hoeckel Joachim
INSERT INTO TABV_APIC_NODE_ACCESS (NODE_ID,READRIGHT,WRITERIGHT,GRANTRIGHT,OWNER,NODE_TYPE,USER_ID) VALUES
(-400,'Y','Y','Y','Y','CMPLI_RVW_READ',372516);

INSERT INTO TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE) VALUES
('COMPLI_REPORT_ACCESS_NODE_ID','A special access rights for users to access Download and Import functionality in Compliance Review Dialog','-400');

COMMIT;

------------------------------------------------------------------------------------------------------------------------------------------------------
-- ALM TaskId : 528739 : New error message for DB unique constraint exception while importing WP Resp from same excel sheet but with different profile
------------------------------------------------------------------------------------------------------------------------------------------------------

Insert into T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER) values ('A2L_RESP_IMPORT','UK_CONS_EXCEPTION','Import aborted. There is a responsibility which already exists in the PIDC but with a different alias name.',null);
COMMIT;


------------------------------------------------------------------------------------------------------------------------------------------------------
-- ALM TaskId : 525492 : Hint for QSSD signature in compliance pdf report 
------------------------------------------------------------------------------------------------------------------------------------------------------
-- Move the message COMPLI_REPORT_HINT to T_MESSAGES
delete from TABV_COMMON_PARAMS where PARAM_ID = 'COMPLI_REPORT_HINT';
INSERT INTO T_MESSAGES(GROUP_NAME,NAME,MESSAGE_TEXT) VALUES 
('COMPLI_REVIEW','COMPLI_REPORT_HINT',
'Critical behaviour NOT active (function not active due to other calibration variables).NOT active trigger assessment and release from 2x Experts (Project + CoC)
Critical behaviour Active (Disclosed to authorities OR not disclosed).Active trigger an evaluation via codex case.Release 1x BST + Signatures according to ALM CES Process
In general, all descriptions shall be done in clear text to enable to understand for signature. A link to codex case shall be added as reference when already available.');

-- New message for the requirement
INSERT INTO T_MESSAGES(GROUP_NAME,NAME,MESSAGE_TEXT) 
  VALUES 
    ('COMPLI_REVIEW','COMPLI_REPORT_QSSD_HINT','(5) Confirmation by Project : Customer is responsible for this calibration and has been informed with External Report about the deviation');

COMMIT;

------------------------------------------------------------------------------------------------------------------------------------------------------
-- ALM TaskId : 535443 : Improvement task- Message should be modified when we use the copy version to working set -improve error message during import
------------------------------------------------------------------------------------------------------------------------------------------------------
INSERT INTO T_MESSAGES (GROUP_NAME, NAME, MESSAGE_TEXT) 
  VALUES ('FILE_IMPORT_TO_A2L_WP', 'WP_ALREADY_EXISTS', 'Import is not possible as Work Package details already exist in the A2L file');
INSERT INTO T_MESSAGES (GROUP_NAME, NAME, MESSAGE_TEXT) 
  VALUES ('FC2WP_IMPORT_TO_A2L_WP', 'WP_ALREADY_EXISTS', 'Import is not possible as Work Package details already exist in the A2L file');

COMMIT;

------------------------------------------------------------------------------------------------------------------------------------------------------
-- ALM TaskId : 535176 : During Compare Hex - if an value is not set for an attribute when we try to set by seeing the error message then an Refresh issue occurred
------------------------------------------------------------------------------------------------------------------------------------------------------
update t_messages set MESSAGE_TEXT = 'There are COMPLI/QSSD failures in the HEX file! The CheckSSD report will be opened.' 
  where name = 'HEX_COMPARE_COMPLI_CHECK' and GROUP_NAME = 'HEX_COMPARE';

COMMIT;
------------------------------------------------------------------------------------------------------------------------------------------------------
-- ALM TaskId : 530956 :  Validate handling of german characters during import of csv is failing
------------------------------------------------------------------------------------------------------------------------------------------------------
INSERT INTO TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE) VALUES 
('FILE_ENCODING_CHARSET','Each Charset seperated by semicolon(;) contains key value pair seperated by colon (:). Key is a encoding name available in notepad++ and value is the corresponding charset to encode the file','ANSI : WINDOWS-1252;WESTERN_EUROPE : ISO-8859-1');

COMMIT;

spool off
