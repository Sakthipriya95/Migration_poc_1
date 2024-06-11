spool c:\temp\30_Table_Data.log

--------------------------------------------------------------------------------------------------------------------
--584126: Dev: Excel Report Enhancements in 100% CDFx Export
--------------------------------------------------------------------------------------------------------------------

Insert into TABV_COMMON_PARAMS (PARAM_ID, PARAM_DESC, PARAM_VALUE, VERSION) 
values(
      'CDFX_100_DELIVERY_UC_IDS'
    , 'Specify the Use Case IDs(comma separated) to identify the attributes for 100% CDFx delivery'
    , '<comma separated use case IDs>'
    , 1
    ); 
COMMIT;

--------------------------------------------------------------------------------------------------------------------
--585074: Impl:Compliance Report: Signature Panel for BEG should be different from PS-EC  
--New Common param added to store the value id of BEG Cal Project
--------------------------------------------------------------------------------------------------------------------
Insert into TABV_COMMON_PARAMS (PARAM_ID, PARAM_DESC, PARAM_VALUE, VERSION) 
values(
      'BEG_CAL_PROJ_ATTR_VAL_ID'
    , 'Value ID of ''BEG CAL project'' in ''Project Organisation'' attribute'
    , '<specify the value id>'
    , 1
    ); 
COMMIT;  

--------------------------------------------------------------------------------------------------------------------
--588369: Error message should show responsibilities and alias names which abort the import from excel
--------------------------------------------------------------------------------------------------------------------
Insert into T_MESSAGES (GROUP_NAME, NAME, MESSAGE_TEXT) 
values (
      'A2L_RESP_IMPORT'
    , 'T_PIDC_WP_RESP_UK'
    , 'Import aborted. One or more responsibilities already exist in the PIDC with different alias names.\nGiven below are the existing records in iCDM (<responsibility name> : <alias name>){0}');
COMMIT;

--------------------------------------------------------------------------------------------------------------------
--589930: Impl - Performance improvement in PK_PAR2WP_PKG
--------------------------------------------------------------------------------------------------------------------
INSERT INTO T_JOBS (JOB_ID,
                    JOB_NAME,
                    RESPONSIBLE_MAIL_ADR,
                    SEND_MAIL_ON_ERROR,
                    SEND_MAIL_ON_FINISH,
                    SAVE_LOG_ON_ERROR_ONLY,
                    LOG_DEBUG_MESSAGES,
                    LOG_TRACE_MESSAGES)
SELECT seqv_attributes.nextval, 'pk_par2wp_copy', 'michael.heinrich2@de.bosch.com', 'N', 'N', 'N', 'Y', 'N' from dual;


--------------------------------------------------------------------------------------------------------------------
--590492: Create node access rights from Admin operations (e.g. Node Access Management Editor)
--------------------------------------------------------------------------------------------------------------------
INSERT into tabv_common_params 
    (PARAM_ID, PARAM_DESC, PARAM_VALUE, VERSION) 
values (
        'ADMIN_ACCESS_NODE_ID'
        ,'Access to special administration activities'
        ,-500
        ,1
        );
COMMIT;

--Create node access rights for users for admin users
insert into tabv_apic_node_access
    (node_id, readright, writeright, grantright, owner, node_type, user_id)
select -500, 'Y', 'N', 'N', 'N', 'SPECIAL_ADMIN', user_id
    from tabv_apic_users
    where username in ('HEF2FE', 'IMI2SI', 'EMS4KOR', 'RHM5COB', 'GGE6COB', 'BNE4COB');

COMMIT;

--------------------------------------------------------------------------------------------------------------------
--594398: Error code when A2L File name is not present in the metadata (JSON file)
--------------------------------------------------------------------------------------------------------------------
UPDATE T_MESSAGES
SET
    NAME = 'A2L_MISSING_IN_METADATA',
    MESSAGE_TEXT = 'A2L File name is not present in the metadata (JSON file)' 
WHERE
    NAME = 'A2L_MISSING_IN_JSON';
    

--------------------------------------------------------------------------------------------------------------------
--591021: Fix: It is not possible to import review comments with compli/qssd failed label with score 9
--------------------------------------------------------------------------------------------------------------------
Insert into T_MESSAGES (GROUP_NAME, NAME, MESSAGE_TEXT) 
values (
      'RVW_RESULT_COMMENT_IMPORT'
    , 'EMPTY_COMMENT'
    , 'Empty comments cannot be imported');
COMMIT;

------------------------------------------------------------------------------------------------------------------
--Version update to 2021.6.0 script 
------------------------------------------------------------------------------------------------------------------
UPDATE TABV_COMMON_PARAMS SET PARAM_VALUE = '2021.6.0' WHERE PARAM_ID='ICDM_CLIENT_VERSION';
COMMIT;

--------------------------------------------------------------------------------------------------------------------
--599644: 'Division' attribute old messages to be renamed to 'iCDM Questionnaire config'
--------------------------------------------------------------------------------------------------------------------
UPDATE TABV_COMMON_PARAMS
    SET PARAM_ID = 'ICDM_QNAIRE_CONFIG_ATTR',
        PARAM_DESC = 'iCDM Questionnaire Config Attribute ID'
    where PARAM_ID = 'PIDC_DIVISION_ATTR';
COMMIT;

--------------------------------------------------------------------------------------------------------------------
--602391: Show only questionnaires that have questions in their active version
--------------------------------------------------------------------------------------------------------------------
Insert into T_MESSAGES (GROUP_NAME, NAME, MESSAGE_TEXT) 
values (
      'QNAIRE_DEF'
    , 'WP_WITHOUT_QNAIRE'
    , 'The following Work Package(s) do not have Questionnaire: {0}');
COMMIT;

spool off