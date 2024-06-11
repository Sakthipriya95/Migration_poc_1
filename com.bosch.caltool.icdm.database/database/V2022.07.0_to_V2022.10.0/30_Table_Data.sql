spool c:\temp\30_Table_Data.log

------------------------------------------------------------------------------------------------------------------
--Task 664672: Version update to 2022.7.0 script 
------------------------------------------------------------------------------------------------------------------
UPDATE T_MESSAGES SET MESSAGE_TEXT = 'ARC Release necessary, Value calibrated, further test required' WHERE GROUP_NAME = 'REVIEW_SCORE' AND NAME = 'SCORE_6';
COMMIT;

------------------------------------------------------------------------------------------------------------------
--Task 685900 : Download OSS document
------------------------------------------------------------------------------------------------------------------
INSERT INTO TABV_ICDM_FILES(NODE_ID,NODE_TYPE,FILE_NAME,FILE_COUNT) values (-11,'TEMPLATES','OSS_Document.zip',1);
COMMIT;

INSERT INTO TABV_ICDM_FILE_DATA
    (FILE_DATA_ID, FILE_ID, FILE_DATA)
  SELECT SEQV_ATTRIBUTES.NEXTVAL, FILE_ID, EMPTY_BLOB() 
    FROM TABV_ICDM_FILES 
    WHERE NODE_ID = -11;   
COMMIT;

-- IMPORTANT : Once the filedata with empty blob is inserted then the file has to be 
--             uploaded from sql developer directly

-- Store file ID of OSS document in common params table
INSERT INTO TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE,VERSION) 
  SELECT 'OSS_FILE_ID','OSS Document',FILE_ID, 1 
    FROM TABV_ICDM_FILES 
    WHERE NODE_ID = -11;

COMMIT;

------------------------------------------------------------------------------------------------------------------
--Task 688556 :  Adding Support Dashboard Link in About Dialog
------------------------------------------------------------------------------------------------------------------
INSERT INTO TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE,VERSION) 
  VALUES ('SUPPORT_DASHBOARD_LINK','Link for support Dashboard','https://si-cdm05.de.bosch.com:8343/SupportDashboard/',1);
INSERT INTO TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE,VERSION) 
  VALUES ('SUPPORT_DASHBOARD_VISIBILITY','Configure visibility of Support Dashboard Link (possible values : Y or N)','N',1);

COMMIT;

------------------------------------------------------------------------------------------------------------------
--Version update to 2022.10.0 script 
------------------------------------------------------------------------------------------------------------------
UPDATE TABV_COMMON_PARAMS SET PARAM_VALUE = '2022.10.0' WHERE PARAM_ID='ICDM_CLIENT_VERSION';
COMMIT;

------------------------------------------------------------------------------------------------------------------
--Task 695409: Modify Error Message 'WP details are not copied...' 
------------------------------------------------------------------------------------------------------------------
UPDATE T_MESSAGES SET MESSAGE_TEXT = 'Work package details are not copied because they are already equal in the working set and the source version.' where GROUP_NAME = 'COPY_A2L' and NAME = 'COPY_MAPPINGS_INVALID';
COMMIT;

spool off