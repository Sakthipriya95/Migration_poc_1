spool c:\temp\30_Table_Data.log

--------------------------------------------------------------------------------------------------------------------
--571991: Dev: Handling of Review Result Deletion after used by CDFX Delivery
--------------------------------------------------------------------------------------------------------------------

INSERT INTO T_MESSAGES (GROUP_NAME, NAME, MESSAGE_TEXT)
    VALUES ('REVIEW_RESULT', 'USED_IN_CDFX_DELIVERY', 'Review Type change is not allowed as this review is part of one or more 100% CDFx Delivery event(s)');

commit;


------------------------------------------------------------------------------------------------------------------
-- ALM TaskId : 560511 : insert tooltip for 'Derive from function' check box
------------------------------------------------------------------------------------------------------------------
Insert into T_MESSAGES(GROUP_NAME,NAME,MESSAGE_TEXT) values ('A2L_COPY','TOOLTIP_DERIVE_FROM_FUNCTION','Derive WP/Responsibility for new parameters in destination from function(s) with distinct WP/Responsibility mapping in source');
COMMIT;

-------------------------------------------------------------------------------------------------------------------
--ALM Task Id : 572308 : Compliance Report : Add new link
-------------------------------------------------------------------------------------------------------------------
INSERT INTO TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE) 
VALUES 
  (
    'COMPLI_REPORT_CSSD_CODEX_LINK',
    'Link to PS-EC Codex Reporting Tool for active C-SSD deviation with “json file”',
    'https://ps-codex.bosch.com/CompliSSD/'
  );
COMMIT;

-------------------------------------------------------------------------------------------------------------------
--ALM Task Id : 578067 : 2FA integration in iCDM - Insert parameter in Tabv_Common_Params table
-------------------------------------------------------------------------------------------------------------------
INSERT INTO TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE) 
VALUES 
  (
    'TWOFA_CHECK_LEVEL',
    'Validation level for 2FA. Options : N - None, W - Warn, B - Block(Access not allowed)',
    'W'
  );
COMMIT;
-------------------------------------------------------------------------------------------------------------------
--ALM Task Id : 578067 : 2FA integration in iCDM - Insert messages into T_Messages table
-------------------------------------------------------------------------------------------------------------------
Insert into T_MESSAGES(GROUP_NAME,NAME,MESSAGE_TEXT) values ('2FA','ERROR_MSG','You are not logged in with 2FA. Access to iCDM is not allowed.');
Insert into T_MESSAGES(GROUP_NAME,NAME,MESSAGE_TEXT) values ('2FA','WARNING_MSG','You are not logged in with 2FA. This is tolerated at the moment. Nevertheless use 2FA in future.');
COMMIT;

-------------------------------------------------------------------------------------------------------------------
--ALM Task Id : 578370 : Readiness condition for 100% cdfx export from html file
-------------------------------------------------------------------------------------------------------------------
INSERT INTO TABV_ICDM_FILES (FILE_ID,NODE_ID,NODE_TYPE,FILE_NAME,FILE_COUNT) 
VALUES (SEQV_ATTRIBUTES.NEXTVAL,-9,'TEMPLATES','cdfx_readiness_condition.html',1);

INSERT INTO TABV_ICDM_FILE_DATA (FILE_DATA_ID,FILE_ID,FILE_DATA)
    SELECT SEQV_ATTRIBUTES.NEXTVAL,FILE_ID,EMPTY_BLOB() 
    FROM TABV_ICDM_FILES 
    WHERE NODE_ID = -9;

INSERT INTO TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE) 
    VALUES( 'CDFX_READINESS_FILE_NODE_ID','Node ID of file for 100% CDFx export readiness condition',-9);
        
COMMIT;

-------------------------------------------------------------------------------------------------------------------
--ALM Task Id : 575453 : Consider attribute "Project Status" for the filter that is used when opening a PIDC. If attribute is set to "Quotation" the filter should only show quotation relevant attributes.
-------------------------------------------------------------------------------------------------------------------

INSERT INTO TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE) 
    VALUES( 'QUOT_VALUE_IDS','Attribute value IDs of quotation related values for Project Status attribute','776001666,787180116,765188618,787180117');

COMMIT;

------------------------------------------------------------------------------------------------------------------
-- ALM TaskId : 578083 : Version update to 2021.3.0 script
------------------------------------------------------------------------------------------------------------------      
delete from TABV_COMMON_PARAMS WHERE upper(PARAM_ID) = 'ICDM_CLIENT_VERSION';
Insert into TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE,VERSION) values ('ICDM_CLIENT_VERSION','iCDM Client''s current version','2021.3.0',1); 
COMMIT;

spool off
