spool c:\temp\30_Table_Data.log

------------------------------------------------------------------------------------------------------------------
--627197: Fix : Test Failures - A2LFileDownloadServiceClientTest
------------------------------------------------------------------------------------------------------------------
Insert into T_MESSAGES (GROUP_NAME, NAME, MESSAGE_TEXT) 
  values ('A2L', 'INVALID_VCDM_A2L_FILE_ID', 'Invalid vCDM A2L File ID : {0}');

COMMIT;

------------------------------------------------------------------------------------------------------------------
--628988: Move code that retrieves server work dirs of server group to a new class
------------------------------------------------------------------------------------------------------------------
UPDATE T_MESSAGES 
 SET 
    GROUP_NAME = 'GENERAL',
    MESSAGE_TEXT = 'Server path(s) not configured. Please contact iCDM hotline.'
 WHERE 
    GROUP_NAME = 'COMPLI_REVIEW' and NAME = 'SERVER_PATH_MISSING';

Insert into T_MESSAGES (GROUP_NAME, NAME, MESSAGE_TEXT, ERROR_YN) 
values ('A2L_RESP_MERGE','FILES_NOT_AVAILABLE','Files not available for execution ID : {0}','Y');
   

------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 628146: Par2WP: create work packages from functions only if Default WP exists
------------------------------------------------------------------------------------------------------------------
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
        seqv_attributes.nextval, 'pk_create_wp_from_func.p_create_wp_from_func', 'Michael.Heinrich2@de.bosch.com', 'N', 'N', 'N', 'Y', 'N' 
    from dual;

COMMIT;

------------------------------------------------------------------------------------------------------------------
--615183: vCDM support - 1.19 : update CDM Studio path
------------------------------------------------------------------------------------------------------------------
update TABV_COMMON_PARAMS
    set PARAM_VALUE = PARAM_VALUE || ';Vector vCDM 19.1 SP2\vCDMstudio\Exec64\vCDMStudio4vCDM64.exe'
    where PARAM_ID = 'VCDMSTUDIO_EXE_REL_PATH';

COMMIT;

 ------------------------------------------------------------------------------------------------------------------
--Version update to 2021.12.0 script 
------------------------------------------------------------------------------------------------------------------
UPDATE TABV_COMMON_PARAMS SET PARAM_VALUE = '2021.12.0' WHERE PARAM_ID='ICDM_CLIENT_VERSION';
COMMIT;


spool off