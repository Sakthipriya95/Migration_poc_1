spool c:\temp\30_Table_Data.log

------------------------------------------------------------------------------------------------------------------
--Task 699569: impl: Improvements in ‘ARCReleaseDisplayDialog’
--Configuring the messages related to ARC and WP finished to be fetched from DB instead of Hardcoding 
------------------------------------------------------------------------------------------------------------------

INSERT INTO T_MESSAGES (GROUP_NAME, NAME, MESSAGE_TEXT)
    VALUES ('REVIEW_RESULT',
            'ARC_CONFIRMATION_INFO', 
            'The following parameters have score greater than 0 and less than 8/9,\n All these parameters must be confirmed as ARC Released for the Work Package to be "Finished".'
            );

COMMIT;

INSERT INTO T_MESSAGES (GROUP_NAME, NAME, MESSAGE_TEXT)
    VALUES ('REVIEW_RESULT',
            'FINISHED_CONFIRMATION_MSG', 
            'Do you want to mark your Work Package as "Finished"? '
            );
COMMIT;

INSERT INTO T_MESSAGES (GROUP_NAME, NAME, MESSAGE_TEXT)
    VALUES ('REVIEW_RESULT',
            'NOT_REVIEWED_PARAMS_ERROR_MSG', 
            'Review contains parameters with score 0, Work Package cannot be marked as "Finished".'
            );
COMMIT;

INSERT INTO T_MESSAGES (GROUP_NAME, NAME, MESSAGE_TEXT)
    VALUES ('REVIEW_RESULT',
            'REVIEW_NOT_LOCKED_ERROR_MSG', 
            'Review should be locked to mark a Work Package as "Finished".'
            );
COMMIT;

INSERT INTO T_JOBS (JOB_ID,
                    JOB_NAME,
                    RESPONSIBLE_MAIL_ADR,
                    SEND_MAIL_ON_ERROR,
                    SEND_MAIL_ON_FINISH,
                    SAVE_LOG_ON_ERROR_ONLY,
                    LOG_DEBUG_MESSAGES,
                    LOG_TRACE_MESSAGES
                   )
    SELECT 
        seqv_attributes.nextval, 'pk_create_wp_from_func.p_updateResp', 'Michael.Heinrich2@de.bosch.com', 'N', 'N', 'N', 'Y', 'N' 
    from dual;
COMMIT;


---------------------------------------------------------------------  
--------------------------------------------------------------------------------------------------------------------------------
--  ALM Task : 637826 - Server Side changes , Table Modification for linking WP-RESP
--  Inserting display messages for Questionnaire Linking in t_messages table
--------------------------------------------------------------------------------------------------------------------------------
INSERT INTO T_MESSAGES (GROUP_NAME, NAME, MESSAGE_TEXT)
    VALUES ('REVIEW_QUESTIONNAIRES','QNAIRE_RESP_ALREADY_LINKED', 'The Questionnaire Response is already linked, If it is again linked from here, the original Questionnaire Response will be linked');
COMMIT;
    
    INSERT INTO T_MESSAGES (GROUP_NAME, NAME, MESSAGE_TEXT)
    VALUES ('REVIEW_QUESTIONNAIRES','NO_WP_IN_VARIANT', 'The variant doesn''t contain the workpackage');
COMMIT;
    
INSERT INTO T_MESSAGES (GROUP_NAME, NAME, MESSAGE_TEXT)
    VALUES ('REVIEW_QUESTIONNAIRES','UNLINK_PRIMARY_LINK_NOT_POSSIBLE', 'It is not possible to un-link primary variant');
COMMIT;

---------------------------------------------------------------------  
--------------------------------------------------------------------------------------------------------------------------------
--  ALM Task : 713885 - impl : Adding Review Result and Questionnaire related files in Data Assessment combined zip file
--  Inserting TABV_COMMON_PARAMS to fetch the attribute id of customer/brand attribute
--------------------------------------------------------------------------------------------------------------------------------
INSERT into TABV_COMMON_PARAMS (PARAM_ID, PARAM_DESC, PARAM_VALUE, VERSION)
    VALUES (
        'CUSTOMER_OR_BRAND_ATTR_ID'
      , 'Customer/Brand Attribute ID'
      , (select ATTR_ID from TABV_ATTRIBUTES where ATTR_NAME_ENG = 'Customer/Brand')
      , 1
    );

COMMIT;

 ------------------------------------------------------------------------------------------------------------------
--Version update to 2023.3.0 script 
------------------------------------------------------------------------------------------------------------------
UPDATE TABV_COMMON_PARAMS SET PARAM_VALUE = '2023.3.0' WHERE PARAM_ID='ICDM_CLIENT_VERSION';
COMMIT;


---------------------------------------------------------------------  
--------------------------------------------------------------------------------------------------------------------------------
--  ALM Task : 718916: Impl: Performance improvement for the creation of compare HEX pdf report
-- Inserting the info messages that needs to be displayed duirng data assessment baseline creation and file download
-- to T_MESSAGES table 
--------------------------------------------------------------------------------------------------------------------------------
INSERT INTO T_MESSAGES (GROUP_NAME, NAME, MESSAGE_TEXT)
    VALUES ('DATA_ASSESSMENT','BASELINE_CREATION', 'Data Assessment Baseline is created. Baseline File creation is in progress. You will be notified once the process is completed.');
COMMIT;

INSERT INTO T_MESSAGES (GROUP_NAME, NAME, MESSAGE_TEXT)
    VALUES ('DATA_ASSESSMENT','FILE_STATUS_IN_PROGRESS', 'Creation of baseline files is still in progress. Please try again after some time.');
COMMIT;

INSERT INTO T_MESSAGES (GROUP_NAME, NAME, MESSAGE_TEXT)
    VALUES ('DATA_ASSESSMENT','FILE_STATUS_FAILED', 'Creation of baseline files failed and hence the file could not be downloaded.');
COMMIT;

INSERT INTO T_MESSAGES (GROUP_NAME, NAME, MESSAGE_TEXT)
    VALUES ('DATA_ASSESSMENT','FILE_STATUS_NOT_AVAILABLE', 'Baseline files do not exist for this baseline.');
COMMIT;

--------------------------------------------------------------------------------------------------------------------------------
--  ALM Task : 710713: Focus Matrix: Hide tab page when no entry exists and tabv_common_param is active
--------------------------------------------------------------------------------------------------------------------------------

INSERT INTO TABV_COMMON_PARAMS (PARAM_ID, PARAM_DESC, PARAM_VALUE, VERSION) 
    VALUES ('FOCUS_MATRIX_ENABLED', 'Show or hide focus matrix for PIDC without Focus Matrix entries', 'Y', '1');
COMMIT;

spool off