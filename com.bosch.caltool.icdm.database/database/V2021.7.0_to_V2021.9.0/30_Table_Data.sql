spool c:\temp\30_Table_Data.log

--------------------------------------------------------------------------------------------------------------------
--600870: Indicator message in Review results editor that questionnaires are not filled
--------------------------------------------------------------------------------------------------------------------
Insert into T_MESSAGES (GROUP_NAME, NAME, MESSAGE_TEXT) 
values (
      'REVIEW_RESULT'
    , 'QUES_MSG_IN_RVW_RESULT'
    , 'Questionnaires for this review are not completely answered');
COMMIT;

--------------------------------------------------------------------------------------------------------------------
--594506: Impl - New compliance review service that accepts vCDM DST ID as input
--------------------------------------------------------------------------------------------------------------------
Insert into T_MESSAGES (GROUP_NAME, NAME, MESSAGE_TEXT) 
values (
      'COMPLI_REVIEW'
    , 'DST_MISSING'
    , 'vCDM DST ID is missing');
COMMIT;

--------------------------------------------------------------------------------------------------------------------
--615272: Errors needs to reported with error codes-Fix
--------------------------------------------------------------------------------------------------------------------
Insert into T_MESSAGES (GROUP_NAME, NAME, MESSAGE_TEXT) 
values (
      'COMPLI_REVIEW'
    , 'VCDM_DST_INVALID'
    , 'Not a valid vCDM DST ID');

Insert into T_MESSAGES (GROUP_NAME, NAME, MESSAGE_TEXT) 
values (
      'COMPLI_REVIEW'
    , 'VARIANT_REQUIRED'
    , 'Variant is defined for the PIDC. Variant ID is required for Review');
COMMIT;

--------------------------------------------------------------------------------------------------------------------
--606392: 2. Remove the entry "iCDM Community Page" from the Help menu.
--------------------------------------------------------------------------------------------------------------------
DELETE FROM TABV_COMMON_PARAMS where PARAM_ID='ICDM_COMMUNITY_LINK';
COMMIT;

--------------------------------------------------------------------------------------------------------------------
--606285: Load FC2WP assignments in A2L files 
--------------------------------------------------------------------------------------------------------------------   
DECLARE
  v_load_fc2wp_a2l_attr_id NUMBER;
  v_load_fc2wp_a2l_attr_grp_id NUMBER;
 
BEGIN
  --Generate attribute ID
  SELECT seqv_attributes.nextval INTO v_load_fc2wp_a2l_attr_id FROM dual;
  SELECT GROUP_ID INTO v_load_fc2wp_a2l_attr_grp_id FROM TABV_ATTR_GROUPS where GROUP_NAME_ENG = 'Project Information';
    
  --Insert new attribute 'Load FC2WP assignments in A2L files' in TABV_ATTRIBUTES
  INSERT INTO TABV_ATTRIBUTES(
        ATTR_ID,
        ATTR_NAME_ENG,
        ATTR_DESC_ENG,
        GROUP_ID,
        VALUE_TYPE_ID,
        NORMALIZED_FLAG,
        DELETED_FLAG,
        MANDATORY,
        UNITS,
        PART_NUMBER_FLAG,
        SPEC_LINK_FLAG,
        ATTR_SECURITY,
        VALUE_SECURITY,
        EADM_NAME)
    VALUES(
        v_load_fc2wp_a2l_attr_id,
        'Load FC2WP assignments automatically in A2L files',
        'FC2WP mappings to be imported while assigning new A2L files', 
        v_load_fc2wp_a2l_attr_grp_id,
        4,
        'Y',
        'N',
        'N',
        '-',
        'N',
        'N',
        'E',
        'E',
        'Load_FC2WP_assignments_automatically_in_A2L_files');

  --Insert parameter 'LOAD_FC2WP_IN_A2L_ATTR' in TABV_COMMON_PARAMS to hold 'Load FC2WP assignments in A2L files' attr id
  INSERT INTO TABV_COMMON_PARAMS(
        PARAM_ID, 
        PARAM_DESC, 
        PARAM_VALUE, 
        VERSION)
    VALUES(
        'LOAD_FC2WP_IN_A2L_ATTR', 
        'Attribute ID of "Load FC2WP assignments automatically in A2L files" attribute', 
        TO_CHAR(v_load_fc2wp_a2l_attr_id), 
        1);
        
  --Insert boolean value 'T' in TABV_ATTR_VALUES for attribute 'Load FC2WP assignments in A2L files'
  INSERT INTO TABV_ATTR_VALUES(
        ATTR_ID, 
        VALUE_DESC_ENG,  
        BOOLVALUE, 
        DELETED_FLAG, 
        CLEARING_STATUS) 
    VALUES(
        v_load_fc2wp_a2l_attr_id, 
        'TRUE', 
        'T', 
        'N', 
        'Y');
     
  COMMIT;
  
END;
/

---------------------------------------------------------------------------------------------------------------------
--585189: Populate UI components with service output
---------------------------------------------------------------------------------------------------------------------
Insert into TABV_COMMON_PARAMS (PARAM_ID, PARAM_DESC, PARAM_VALUE, VERSION) 
values(
      'UNMAP_A2L_SUBJECT'
    , 'Mail Subject for unmapping A2L from PIDC Version'
    , '#A2l - Unmap A2L from PIDC Version'
    , 1
    ); 
COMMIT;

---------------------------------------------------------------------------------------------------------------------
--594535: Adding t_messages entry for compli review attached error message while unmapping A2L
---------------------------------------------------------------------------------------------------------------------
Insert into T_MESSAGES(GROUP_NAME,NAME,MESSAGE_TEXT) 
values ('UNMAP_A2L','COMPLI_REVIEWS_ATTACHED', 'A2L file cannot be unmapped as there are compliance reviews attached to it');
COMMIT;

------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 594541: Server side changes - for deletion of A2L related DB entries
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
        seqv_attributes.nextval, 'pk_unmap_a2l.P_UNMAP_A2L', 'Michael.Heinrich2@de.bosch.com', 'N', 'N', 'N', 'Y', 'N' 
    from dual;

---------------------------------------------------------------------------------------------------------------------
--594535: Adding t_messages entry for Cdfx reviews attached error message while unmapping A2L
---------------------------------------------------------------------------------------------------------------------
Insert into T_MESSAGES(GROUP_NAME,NAME,MESSAGE_TEXT) values ('UNMAP_A2L','CDFX_DELIVERIES_ATTACHED','A2L file cannot be unmapped as there are 100% CDFX exports attached to it');
COMMIT;

---------------------------------------------------------------------------------------------------------------------
--594535: Adding t_messages entry for not mapped A2L files
---------------------------------------------------------------------------------------------------------------------
Insert into T_MESSAGES(GROUP_NAME,NAME,MESSAGE_TEXT) values ('UNMAP_A2L','NOT_MAPPED_TO_PIDC_VERSION','A2L file is not mapped to PIDC Version');
COMMIT;

---------------------------------------------------------------------------------------------------------------------------------
--616976: Improvement in condition to lock a review result and in condition to open window 'Questionnaire Responses' when user tries to lock the review result
----------------------------------------------------------------------------------------------------------------------------------
insert into tabv_common_params(PARAM_ID,PARAM_DESC,PARAM_VALUE)
VALUES 
  (
    'DIV_IDS_FOR_REVIEW_LOCK_CHECK',
    'Specify the attribute value IDs(comma separated) to identify the organisations for which questionnaire responses needs to be filled before locking of review result',
    '<Enter the relevant division attribute value ids for "iCDM Questionnaire Config" attribute . Ex:789053017,787372419  >'
  );
COMMIT;

---------------------------------------------------------------------------------------------------------------------
--611781: Renaming column Result to Answer in Questionnaire response editor
---------------------------------------------------------------------------------------------------------------------
UPDATE T_MESSAGES 
SET 
    MESSAGE_TEXT = 'Answer' 
where 
    GROUP_NAME = 'REVIEW_QUESTIONNAIRES' 
    and NAME='RESULT';
    
COMMIT;

---------------------------------------------------------------------------------------------------------------------
--611789: Deleting access rights related entries from database
---------------------------------------------------------------------------------------------------------------------
delete from TABV_APIC_NODE_ACCESS where node_id = (select param_value from tabv_common_params where param_id='CDFX_DELIVERY_NODE_ID');
delete from tabv_common_params where param_id = 'CDFX_DELIVERY_NODE_ID';


 ------------------------------------------------------------------------------------------------------------------
--Version update to 2021.9.0 script 
------------------------------------------------------------------------------------------------------------------
UPDATE TABV_COMMON_PARAMS SET PARAM_VALUE = '2021.9.0' WHERE PARAM_ID = 'ICDM_CLIENT_VERSION';
COMMIT;

------------------------------------------------------------------------------------------------------------------
--614430: Impl : Implement logic to support import of already existing WP into A2L using FC2WP 
------------------------------------------------------------------------------------------------------------------
UPDATE T_MESSAGES
SET
    MESSAGE_TEXT = 'The work packages in this A2L and the FC2WP list are already equal. There were no changes since you last loaded data from FC2WP. Thus, no update of existing data has been done.' 
where 
    GROUP_NAME = 'FC2WP_IMPORT_TO_A2L_WP' 
    and NAME='WP_ALREADY_EXISTS';
COMMIT;

spool off