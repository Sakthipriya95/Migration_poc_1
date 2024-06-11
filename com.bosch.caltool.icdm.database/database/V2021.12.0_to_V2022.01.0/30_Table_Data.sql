spool c:\temp\30_Table_Data.log


--------------------------------------------------------------------------------------------------------------------
--628188: Allow exceptions when checking for same attribute values when linking reviews to other variants
--------------------------------------------------------------------------------------------------------------------
Insert into TABV_COMMON_PARAMS (PARAM_ID, PARAM_DESC, PARAM_VALUE, VERSION) 
values(
      'RVW_ATTR_IDS_NOT_REL_FOR_LINK'
    , 'Specify the Attribute IDs(comma separated) to identify the review attributes that are not relevant for variant linking'
    , '399,400'
    , 1
    ); 
COMMIT;

--------------------------------------------------------------------------------------------------------------------
--622082: A Source questionnaire should be link-able to many variant
--------------------------------------------------------------------------------------------------------------------
Update T_MESSAGES set MESSAGE_TEXT = 'Questionnaire {0} of {1} is already linked with the source variant' WHERE GROUP_NAME = 'RVW_QNAIRE_RESP' AND NAME = 'LINK_VAR_NOT_ALLOWED';
COMMIT;

INSERT INTO TABV_COMMON_PARAMS(PARAM_ID,PARAM_DESC,PARAM_VALUE)
SELECT 'FUELTYPE_ATTR_ID', 'Fuel Type Attribute ID', ATTR_ID from TABV_ATTRIBUTES where ATTR_NAME_ENG = 'Fuel Type';
COMMIT;


 ------------------------------------------------------------------------------------------------------------------
--Version update to 2022.1.0 script 
------------------------------------------------------------------------------------------------------------------
UPDATE TABV_COMMON_PARAMS SET PARAM_VALUE = '2022.1.0' WHERE PARAM_ID='ICDM_CLIENT_VERSION';
COMMIT;

--------------------------------------------------------------------------------------------------------------------
--642269: Impl - Load hex file using EASEEService for checked-out datasets
--------------------------------------------------------------------------------------------------------------------
INSERT INTO T_MESSAGES 
    (GROUP_NAME, NAME, MESSAGE_TEXT, ERROR_YN, CAUSE, SOLUTION) 
VALUES
(
    'COMPLI_REVIEW', 
	'DST_INPUT_MISSING_CHECKED_OUT_DST',
	'For checked-out DSTs, APRJ Name, APRJ Revision, DST Name, DST Revision are mandatory.',
	'Y',
	'APRJ Name (and/or) APRJ Revision (and/or) DST Name (and/or) DST Revision missing',
	'Provide values for APRJ Name, APRJ Revision, DST Name, DST Revision'
);
COMMIT;




spool off