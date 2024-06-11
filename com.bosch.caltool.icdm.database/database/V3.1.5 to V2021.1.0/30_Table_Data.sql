spool c:\temp\30_Table_Data.log


------------------------------------------------------------------------------------------------------------------
-- ALM TaskId : 548096 : insert PSR-C user name and 'Review Document Subproject Calibration/PSR-C Link' attribute id in common params table
------------------------------------------------------------------------------------------------------------------      
Insert into TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE,VERSION) values ('ATTR_UPDATE_ACCESS_USERS','Users with access rights to update attribute value in PIDC via external services','PSRC_SYS',1); 
Insert into TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE,VERSION) values ('REVIEWDOC_PSRC_LINK_ATTR_ID','Review Document Subproject Calibration/PSR-C Link Attribute Id','249307',1); 

------------------------------------------------------------------------------------------------------------------
-- ALM TaskId : 548096 : insert error messages for error codes 
------------------------------------------------------------------------------------------------------------------
Insert into T_MESSAGES(GROUP_NAME,NAME,MESSAGE_TEXT) values ('PIDC_ATTR_UPDATE_EXT','INVALID_USER','Insufficient privileges to do this operation');
Insert into T_MESSAGES(GROUP_NAME,NAME,MESSAGE_TEXT) values ('PIDC_ATTR_UPDATE_EXT','ATTR_IN_SUB_VAR_LEVEL','This attribute is in sub-variant level in PIDC. Please use iCDM tool to make this update.');
Insert into T_MESSAGES(GROUP_NAME,NAME,MESSAGE_TEXT) values ('PIDC_ATTR_UPDATE_EXT','PROJ_ATTR_INVISIBLE','The attribute is not visible in PIDC due to attribute dependency');
Insert into T_MESSAGES(GROUP_NAME,NAME,MESSAGE_TEXT) values ('PIDC_ATTR_UPDATE_EXT','VAR_ATTR_INVISIBLE','The attribute is not visible in PIDC variant due to attribute dependency');
Insert into T_MESSAGES(GROUP_NAME,NAME,MESSAGE_TEXT) values ('PIDC_ATTR_UPDATE_EXT','PROJ_ATTR_NOT_IN_RIGHT_LEVEL','"This attribute is in variant level in PIDC. Please use iCDM tool to make this update.');
Insert into T_MESSAGES(GROUP_NAME,NAME,MESSAGE_TEXT) values ('PIDC_ATTR_UPDATE_EXT','VAR_ATTR_NOT_IN_RIGHT_LEVEL','"Variant id is given but the attribute is in PIDC level. Please use iCDM tool to make this update.');
Insert into T_MESSAGES(GROUP_NAME,NAME,MESSAGE_TEXT) values ('PIDC_ATTR_UPDATE_EXT','NO_PIDC_ID_OR_VAR_ID','At least one input (pidcId or variantId) should be provided');
Insert into T_MESSAGES(GROUP_NAME,NAME,MESSAGE_TEXT) values ('PIDC_ATTR_UPDATE_EXT','ATTR_ID_NULL','attributeId is mandatory');
Insert into T_MESSAGES(GROUP_NAME,NAME,MESSAGE_TEXT) values ('PIDC_ATTR_UPDATE_EXT','ATTR_NOT_SUPPORTED','Modification of this attribute is not supported. Kindly use iCDM client to edit the value.');
Insert into T_MESSAGES(GROUP_NAME,NAME,MESSAGE_TEXT) values ('PIDC_ATTR_UPDATE_EXT','ATTR_DELETED','Invalid attribute. Attribute is deleted');
Insert into T_MESSAGES(GROUP_NAME,NAME,MESSAGE_TEXT) values ('PIDC_ATTR_UPDATE_EXT','GROUPED_ATTR','This is a grouped attribute. Modification of this attribute is not supported. Kindly use iCDM client to edit the value.');
Insert into T_MESSAGES(GROUP_NAME,NAME,MESSAGE_TEXT) values ('PIDC_ATTR_UPDATE_EXT','PREDEFINED_ATTR','This is a pre-defined attribute, part of a grouped attribute definition. Modification of this attribute is not supported. Kindly use iCDM client to edit the value.');
Insert into T_MESSAGES(GROUP_NAME,NAME,MESSAGE_TEXT) values ('PIDC_ATTR_UPDATE_EXT','NOT_VALID_USED_FLAG','Used flag value is invalid. Expected values (Yes/No/???)');
Insert into T_MESSAGES(GROUP_NAME,NAME,MESSAGE_TEXT) values ('PIDC_ATTR_UPDATE_EXT','INVALID_HYPERLINK','Invalid hyperlink');
Insert into T_MESSAGES(GROUP_NAME,NAME,MESSAGE_TEXT) values ('PIDC_ATTR_UPDATE_EXT','PIDC_DELETED','The given PIDC is deleted');
Insert into T_MESSAGES(GROUP_NAME,NAME,MESSAGE_TEXT) values ('PIDC_ATTR_UPDATE_EXT','VARIANT_DELETED','The given variant is deleted');
Insert into T_MESSAGES(GROUP_NAME,NAME,MESSAGE_TEXT) values ('PIDC_ATTR_UPDATE_EXT','INVALID_VARIANT','The given variant does not belong to the given pidc active version');

COMMIT;

spool off
