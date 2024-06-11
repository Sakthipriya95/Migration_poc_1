spool c:\temp\30_Table_Data.log

------------------------------------------------------------------------------------------------------------------
--Task 716517 : Impl : The ER Diagram & DB changes & Entity should be created/updated to Store the Reviews OBD 
--related Flag in Review Result Table
------------------------------------------------------------------------------------------------------------------
-------------------------------PARAM VALUE for all three Common Param  given below will have change in BETA and PRO---------------
INSERT INTO TABV_COMMON_PARAMs(PARAM_ID,PARAM_DESC,PARAM_VALUE) 
            VALUES('OBD_GENERAL_QNAIRE_ID'
            ,'OBD General Questionnaire ID'
            ,'33221210769');--Qnaire ID of OBD General Qnaire in PRO'---- 
COMMIT;

----To be executed only in BETA DB to update OBD qnaire Name as same as in PRO---
---UPDATE T_questionnaire set NAME_ENG = 'OBD General Questionnaire' where QNAIRE_ID IN (SELECT PARAM_VALUE FROM tabv_common_params WHERE PARAM_ID = 'OBD_GENERAL_QNAIRE_ID');

INSERT INTO TABV_COMMON_PARAMs(PARAM_ID,PARAM_DESC,PARAM_VALUE) 
            VALUES('DIV_WITH_SIMPL_GEN_QNAIRE'
                   ,'Attribute Values of divisions for which simplified questionnaries are applicable(comma separated)'
                   ,'24352557878,1110998980');
COMMIT;

INSERT INTO TABV_COMMON_PARAMs(PARAM_ID,PARAM_DESC,PARAM_VALUE) 
            VALUES('DIVISIONS_WITH_OBD_OPTION'
                   ,'Attribute Values of divisions for which OBD option enabled in Review Wizard(comma separated)'
                   ,'24352557878,787372418');
COMMIT;

------------------------------------------------------------------------------------------------------------------
--Task 730153 : Add a simplified General Questionnaire to a data review
------------------------------------------------------------------------------------------------------------------
INSERT INTO TABV_ICDM_FILES(NODE_ID,NODE_TYPE,FILE_NAME,FILE_COUNT) values (-12,'TEMPLATES','simplified_qnaire_declaration_en.html',1);
COMMIT;

INSERT INTO TABV_ICDM_FILE_DATA
    (FILE_DATA_ID, FILE_ID, FILE_DATA)
  SELECT SEQV_ATTRIBUTES.NEXTVAL, FILE_ID, EMPTY_BLOB() 
    FROM TABV_ICDM_FILES 
    WHERE NODE_ID = -12;   
COMMIT;

-- IMPORTANT : Once the filedata with empty blob is inserted then the file has to be 
--             uploaded from sql developer directly

-- Store file ID of simplified Qnaire declaration document in common params table
INSERT INTO TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE,VERSION) 
  VALUES ('SIMP_QNAIRE_DECLAR_NODE_EN_ID','Node Id of Simplified General Questionnaire Declaration File in English','-12', 1);

COMMIT;


INSERT INTO TABV_ICDM_FILES(NODE_ID,NODE_TYPE,FILE_NAME,FILE_COUNT) values (-13,'TEMPLATES','simplified_qnaire_declaration.html',1);
COMMIT;

INSERT INTO TABV_ICDM_FILE_DATA
    (FILE_DATA_ID, FILE_ID, FILE_DATA)
  SELECT SEQV_ATTRIBUTES.NEXTVAL, FILE_ID, EMPTY_BLOB() 
    FROM TABV_ICDM_FILES 
    WHERE NODE_ID = -13;   
COMMIT;

INSERT INTO TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE,VERSION) 
  VALUES ('SIMP_QNAIRE_DECLAR_NODE_ID','Node Id of Simplified General Questionnaire Declaration File in German','-13', 1);

COMMIT;

------------------------------------------------------------------------------------------------------------------
--Task 737002 : Impl : Archive iCDM data for Data Assessment

------------------------------------------------------------------------------------------------------------------
--Run this insert statement only for IT-BETA
INSERT INTO TABV_COMMON_PARAMS (PARAM_ID, PARAM_DESC, PARAM_VALUE) 
            VALUES ('DATAASSESSMENT_ARCHIVAL_FOLDER'
            , 'Data Assessment Archival Folder path for series release'
            , '\\si0wts0003.de.bosch.com\RBEI\iCDM\DataArchival\IT-BETA');
            
--Run this insert statement only for PRO
INSERT INTO TABV_COMMON_PARAMS (PARAM_ID, PARAM_DESC, PARAM_VALUE) 
            VALUES ('DATAASSESSMENT_ARCHIVAL_FOLDER'
            , 'Data Assessment Archival Folder path for series release'
            , '\\bosch.com\dfsrb\DfsDE\DIV\PS\PC\PS-EC\PA-SH\01_EBT\Si_PS-EC_EBT_Archive\CalibDataManagement\iCDM');

COMMIT;

 ------------------------------------------------------------------------------------------------------------------
--Version update to 2023.6.0 script 
------------------------------------------------------------------------------------------------------------------
UPDATE TABV_COMMON_PARAMS SET PARAM_VALUE = '2023.6.0' WHERE PARAM_ID='ICDM_CLIENT_VERSION';
COMMIT;

------------------------------------------------------------------------------------------------------------------
--Task 739636 : Non-SDOM compliance review proceeds even if attribute value is set to Not Cleared
--Task 741049 : To use same error message in all impact areas (Not Cleared msg)
------------------------------------------------------------------------------------------------------------------
INSERT INTO T_MESSAGES (GROUP_NAME, NAME ,MESSAGE_TEXT ,VERSION)
            VALUES ('COMPLI_REVIEW','PVER_NOT_CLEARED','Compliance review had failed since given PVER name is not ''Cleared'' : {0}',1);
            
  
COMMIT;            
------------------------------------------------------------------------------------------------------------------
--Added new Messages for group validation in Active directory node access 
------------------------------------------------------------------------------------------------------------------

INSERT INTO T_MESSAGES(GROUP_NAME,NAME,MESSAGE_TEXT) VALUES ('ACCESS_RIGHTS',
            'GROUP_ALREADY_ASSIGNED',
            'The Specified Active Directory Group is already assigned to the selected Node. \n To Update access, use the checkbox in table viewer. \n Or Enter a different group name to create new access');
COMMIT;
spool off