spool c:\temp\30_Table_Data.log

---------------------------------------------------------------------
--  ALM Task : 618045 - impl : Add context menu option 'Templates for Comments' to Review Results
---------------------------------------------------------------------

INSERT into T_RVW_COMMENT_TEMPLATES 
    (COMMENT_DESC) 
    values ('Data setting is checked with verification tests, results are ok.');

INSERT into T_RVW_COMMENT_TEMPLATES 
    (COMMENT_DESC) 
    values ('Data setting is carried over from predecessor projects and fulfills also the requirements of current project.');

INSERT into T_RVW_COMMENT_TEMPLATES 
    (COMMENT_DESC) 
    values ('Data setting according to customer request and fulfills all legal, customer and internal requirements.');

INSERT into T_RVW_COMMENT_TEMPLATES 
    (COMMENT_DESC) 
    values ('Review rules are not suitable, (Review rule responsible are to be informed)');

COMMIT;

---------------------------------------------------------------------  

------------------------------------------------------------------------------------------------------------------
--621135: Impl: Handle errors when vCDM file id is null - A2l vCDM download
------------------------------------------------------------------------------------------------------------------
Insert into T_MESSAGES (GROUP_NAME, NAME, MESSAGE_TEXT) 
values ('A2L', 'INCOMPLETE_VCDM_FILE_INFO', 'Incomplete vCDM file information in iCDM for A2L file ID : {0}. Please contact iCDM hotline.');
COMMIT;



 ------------------------------------------------------------------------------------------------------------------
--626605: Version update to 2021.11.0 script 
------------------------------------------------------------------------------------------------------------------
UPDATE TABV_COMMON_PARAMS 
    SET PARAM_VALUE = '2021.11.0' 
    WHERE PARAM_ID = 'ICDM_CLIENT_VERSION';
COMMIT;

spool off