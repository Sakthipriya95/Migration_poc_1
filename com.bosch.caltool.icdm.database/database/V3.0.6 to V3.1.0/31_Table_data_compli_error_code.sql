spool c:\temp\40_Table_data_compli_error_code.log

-----------------------------------------------------------------------------------------------
--  ALM TaskId : 412087: Define unique error codes for iCDM errors 
--  Insert query for T_MESSAGES
-----------------------------------------------------------------------------------------------
Insert into T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER) values ('A2L','PARSE_ERROR','Error while retrieving A2L contents :{0}',null);
Insert into T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER) values ('A2L_COMPLI_PARAMS','A2L_MISSING','Input parameter ''a2lfile'' is mandatory',null);
Insert into T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER) values ('A2L_COMPLI_PARAMS','WEBFLOW_ID_MISSING','Input parameter ''webflowid'' is mandatory',null);
Insert into T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER) values ('FEAVAL','INSUFFICIENT_PIDC_ACCESS','User does not have sufficient acess rights to get the details of the following attributes : {0}',null);
Insert into T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER) values ('FEAVAL','PIDC_ELEMENT_MISSING','PIDC element is mandatory since some of the parameters being reviewed have attribute dependencies',null);
Insert into T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER) values ('FEAVAL','FEATURE_VAL_MISSING','The following Attribute Value(s) do not have equivalent SSD Feature Value(s). Contact iCDM Hotline : {0}',null);
Insert into T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER) values ('COMPLI','SSD_PARSE_ERROR','Error in SSD file. The file paths in server side are | {0}',null);
Insert into T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER) values ('COMPLI','NO_RULE','No rules are available for the selected value for the following Attribute(s)! Please select different value. :\n  {0}\n',null);
Insert into T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER) values ('COMPLI_REVIEW','ATTR_INVISIBLE','The following attribute(s) are not visible due to dependencies. Please configure the Project ID Card appropriately !\n {0}',null);
Insert into T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER) values ('FEAVAL','ATTR_DEFINED_AT_CHILD_LVL','The following attributes are defined at child level : {0}',null);
Insert into T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER) values ('FEAVAL','ATTR_VALUE_NOT_DEFINED','Value not set for the following attribute(s) : {0}',null);
Insert into T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER) values ('COMPLI_REVIEW','HEX_FILES_MISMATCH','The HEX files defined in the meta data is different',null);
Insert into T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER) values ('COMPLI_REVIEW','METADATA_INVALID','Information in meta data is invalid',null);
Insert into T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER) values ('COMPLI_REVIEW','PVER_MISSING','Input parameter ''pverName'' is mandatory',null);
Insert into T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER) values ('COMPLI_REVIEW','PVER_INVALID','Invalid value for PVER Name - {0}',null);
Insert into T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER) values ('COMPLI_REVIEW','PVER_VAR_MISSING','Input parameter ''pverVariant'' is mandatory',null);
Insert into T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER) values ('COMPLI_REVIEW','PVER_VAR_INVALID','The given PVER variant {0} is not valid for the PVER {1}',null);
Insert into T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER) values ('COMPLI_REVIEW','PVER_REV_MISSING','Input parameter ''pverRevision'' is mandatory',null);
Insert into T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER) values ('COMPLI_REVIEW','PVER_REV_TYPE_INVALID','''pverRevision'' must be an integer',null);
Insert into T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER) values ('COMPLI_REVIEW','PVER_REV_INVALID','The given PVER revision {0} is not valid for the given combination of PVER {1} and variant {2}',null);
Insert into T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER) values ('COMPLI_REVIEW','PIDC_VER_VAR_INVALID','Not a valid PIDC Version or Variant',null);
Insert into T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER) values ('COMPLI_REVIEW','PIDC_VAR_DIFF','Variants must belong to the same PIDC Version',null);
Insert into T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER) values ('COMPLI_REVIEW','BC_MISSING','Missing BC nodes : {0}',null);
Insert into T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER) values ('COMPLI_REVIEW','NO_BC_FOR_PVER','No BC information available for the given PVER input',null);
Insert into T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER) values ('FEAVAL','PIDC_ELEM_INVALID','Invalid PIDC element ID : {0}',null);
Insert into T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER) values ('COMPLI_REVIEW','CAL_DATA_INVALID','Error occurred while preparing CalData for parameter',null);
Insert into T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER) values ('COMPLI_REVIEW','HEX_NAME_MISMATCH','HEX file names in the meta data is different',null);
Insert into T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER) values ('COMPLI_REVIEW','HEX_MISMATCH','HEX file(s) provided is different from the details given in metadata',null);
Insert into T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER) values ('COMPLI_REVIEW','A2L_MISMATCH','A2L file is different in meta data',null);
Insert into T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER) values ('COMPLI_REVIEW','A2L_MISSING_IN_JSON','If the A2L File name is not present in the JSON file',null);
Insert into T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER) values ('COMPLI_REVIEW','A2L_INVALID','The given A2L file is corrupted. {0}',null);
Insert into T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER) values ('COMPLI_REVIEW','READ_ERROR','Error in unzipping file',null);
Insert into T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER) values ('COMPLI_REVIEW','HEX_INVALID','Error in validating HEX File',null);
Insert into T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER) values ('COMPLI_REVIEW','NO_BC_PVER','No BC information available for the given PVER input',null);
Insert into T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER) values ('COMPLI_REVIEW','JSON_MISSING','Input JSON file is missing',null);
Insert into T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER) values ('COMPLI_REVIEW','HEX_MISSING','Input HEX file(s) missing',null);
Insert into T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER) values ('COMPLI_REVIEW','A2L_MISSING','A2L file is missing','A2l Datei wird vermisst');
Insert into T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER) values ('COMPLI_REVIEW','FILE_INVALID','The given file is invalid/corrupted',null);
Insert into T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER) values ('COMPLI_REVIEW','MULTIPLE_PIDC_NOT_ALLOWED','Only only PIDC version is allowed in the input',null);
Insert into T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER) values ('COMPLI_REVIEW','OUTPUT_FAILED','Failed to generate review output',null);
Insert into T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER) values ('CHKSSD','PROCESS_ERROR','CheckSSD aborted due to unexpected error',null);
Insert into T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER) values ('CHKSSD','PROCESS_CANCELLED','Processing of CheckSSD has been cancelled',null);
Insert into T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER) values ('GENERAL','JSON_INVALID','Invalid JSON input',null);
Insert into T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER) values ('HEX','PARSE_ERROR','Error in parsing HEX File',null);
Insert into T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER) values ('SSD','MULTIPLE_RULE','Multiple rules found for the parameter(s): {0}',null);
Insert into T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER) values ('SSD','PARSE_ERROR','Failed to parse the SSD file',null);
Insert into T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER) values ('SSD','RELEASE_ERROR','SSD release not allowed. {0}',null);

COMMIT;

spool off
