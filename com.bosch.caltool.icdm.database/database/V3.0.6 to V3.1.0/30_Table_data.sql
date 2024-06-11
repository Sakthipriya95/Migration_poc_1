spool c:\temp\30_Table_data.log

-----------------------------------------------------------------------------------------------
--  ALM TaskId : 421599: Add new attribute value type 
--  Insert query for TABV_ATTR_VALUE_TYPES
-----------------------------------------------------------------------------------------------
INSERT INTO TABV_ATTR_VALUE_TYPES (VALUE_TYPE_ID,VALUE_TYPE) VALUES(6,'ICDM_USER');
COMMIT;

------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 276370: Add new column to T_RVW_PARTICIPANTS TABLE 
--  UPDATE statement for T_RVW_PARTICIPANTS  to set EDIT_FLAG as 'N' to the existing records in T_RVW_PARTICIPANTS
------------------------------------------------------------------------------------------------------------------
UPDATE T_RVW_PARTICIPANTS SET EDIT_FLAG='N' WHERE ACTIVITY_TYPE='P';
COMMIT;

------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 421600: Add new value to TABV_COMMON_PARAMS TABLE 
--  INSERT  for TABV_COMMON_PARAMS  to add new value as PRE_CAL_FULL
------------------------------------------------------------------------------------------------------------------
INSERT INTO TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE,VERSION) VALUES ('PRE_CAL_FULL','Only special users with this ID can get the complete history information for Pre-Calibration data from Review Rules (Common Rules)','-300',1);
COMMIT;

------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 421600: Add new value to T_MESSAGES TABLE 
--  INSERT  for T_MESSAGES  to add new values
------------------------------------------------------------------------------------------------------------------
INSERT INTO T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT) VALUES ('CDFX_EXPORT_DIALOG','PRELIM_CALIBRATED_REMARK_TEXT','Value only for first start-up; no project specific validation has been done, no warranty!');
COMMIT;

------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 444004: Add new value to T_MESSAGES TABLE 
--  INSERT  for T_MESSAGES  to add new values
------------------------------------------------------------------------------------------------------------------
INSERT INTO T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT) VALUES ('REVIEW','SSD_RULE_FETCH_ERROR','Error in fetching rules for the SSD release. {0}');
COMMIT;

------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 439349: Add ERROR CODE with exception to T_MESSAGES TABLE 
--  INSERT  for T_MESSAGES  to add new ERROR CODE
------------------------------------------------------------------------------------------------------------------
Insert into T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT) values ('CDR','PARAM_NOT_FOUND','None of the paramaters found in input file(s) selected!');
Insert into T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT) values ('CDR','HIDDEN_ATTRIBUTES','Review cannot be done since there are hidden attribute(s) in the Project ID Card');
Insert into T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT) values ('SSD','FILE_CREATION_ERROR','Error occured during creation of SSD file. {0}');
Insert into T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT) values ('RULE_SET','INVALID_PARAMETER_SELECTED','None of the parameter(s) selected is available in the Rule Set');
Insert into T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT) values ('CDR','SELECTED_PARAM_NOT_IN_A2L','None of the parameters selected for review is present in the A2L file. Review cannot be done.');
Insert into T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT) values ('CDR','REPEAT_PARAM_EXCEL_REPORT','Param Repeat Excel report | {0}');
COMMIT;

------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 
------------------------------------------------------------------------------------------------------------------
INSERT INTO T_MESSAGES (GROUP_NAME, NAME, MESSAGE_TEXT) VALUES ('GENERAL', 'OBSOLETE_SERVICE', 'This service is no longer available. Contact iCDM Clearing Hotlne for any clarification.');
COMMIT;

------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 461864
------------------------------------------------------------------------------------------------------------------
INSERT INTO T_MESSAGES (GROUP_NAME, NAME, MESSAGE_TEXT) VALUES ('PARAM', 'BLACK_LIST_TOOLTIP', '''Black List Parameters'' are parameters which can have a big impact when calibrated the wrong way. Hence they should be calibrated with utmost care and attention.');
INSERT INTO T_MESSAGES (GROUP_NAME, NAME, MESSAGE_TEXT) VALUES ('PARAM', 'BLACK_LIST_INFO_SINGLE', 'Parameter {0} is a black list parameter. Please review this parameter with care and attention.');
INSERT INTO T_MESSAGES (GROUP_NAME, NAME, MESSAGE_TEXT) VALUES ('PARAM', 'BLACK_LIST_INFO_MULTIPLE', 'The parameters {0} are black list parameters. Please review these parameters with care and attention.');
COMMIT;

------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 469538
------------------------------------------------------------------------------------------------------------------
INSERT INTO T_MESSAGES (GROUP_NAME, NAME, MESSAGE_TEXT) VALUES ('A2L', 'PIDCA2L_MODIFIED_TOOL_TIP', 'There are changes in the working set that are not reflected in any version. A new version of the Label-to-WP assignments should be created after changes in the Working Set are done.');
COMMIT;


------------------------------------------------------------------------------------------------------------------
-- ALM TaskId : 473009 : Version update script
------------------------------------------------------------------------------------------------------------------      
delete from TABV_COMMON_PARAMS WHERE upper(PARAM_ID) = 'ICDM_CLIENT_VERSION';
Insert into TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE,VERSION) values ('ICDM_CLIENT_VERSION','iCDM Client''s current version','3.1.0',1); 
COMMIT;


------------------------------------------------------------------------------------------------------------------
-- ALM TaskId : 477253 : Virtual record tooltip insert script
------------------------------------------------------------------------------------------------------------------ 
INSERT INTO T_MESSAGES (GROUP_NAME, NAME, MESSAGE_TEXT) VALUES ('PARAM', 'VIRTUAL_WP_PARAM_MAPPING_REC_TOOLTIP', 'All details are inherited from the work package definition.');
COMMIT;


------------------------------------------------------------------------------------------------------------------
-- ALM TaskId : Common error code for SSD interface errors
------------------------------------------------------------------------------------------------------------------ 
INSERT INTO T_MESSAGES (GROUP_NAME, NAME, MESSAGE_TEXT) VALUES ('SSD', 'INTERFACE_ERR', 'SSD Error : {0}');
COMMIT;


------------------------------------------------------------------------------------------------------------------
--ALM TaskId : 486223 : Update script to change label to parameter in tooltip

------------------------------------------------------------------------------------------------------------------

UPDATE T_MESSAGES SET MESSAGE_TEXT='There are changes in the working set that are not reflected in any version. A new version of the Parameter-to-WP assignments should be created after changes in the Working Set are done.'
WHERE NAME = 'PIDCA2L_MODIFIED_TOOL_TIP' AND GROUP_NAME = 'A2L';
COMMIT;

------------------------------------------------------------------------------------------------------------------
-- ALM TaskId : 494191 validation msg when we copy mappings from one a2l to another
------------------------------------------------------------------------------------------------------------------ 
INSERT INTO T_MESSAGES (GROUP_NAME, NAME, MESSAGE_TEXT) VALUES ('COPY_A2L', 'COPY_MAPPINGS_INVALID', 'Work Package details are not copied as both source and destination A2L files have similar work package definition');
COMMIT;

------------------------------------------------------------------------------------------------------------------
-- ALM TaskId : 495710 add root grp attribute value id's
------------------------------------------------------------------------------------------------------------------
INSERT INTO TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE,VERSION) VALUES ('WP_ROOT_GROUP','Standard root group for workpackages id','259569',1);
INSERT INTO TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE,VERSION) VALUES ('RESP_ROOT_GROUP','Standard root group for responsibilities id','259568',1);
COMMIT;

spool off
