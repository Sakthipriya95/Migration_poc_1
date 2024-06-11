spool c:\temp\table_data.log

--------------------------------------------------------
--  2015-03-31
-------------------------------------------------------- 

--------------------------------------------------------
--- iCDM-1345 - Additional tooltip messages for PIDC
--------------------------------------------------------

insert into T_MESSAGES(GROUP_NAME,NAME,MESSAGE_TEXT) values ('PIDC_EDITOR','INVISIBLE_ATTR_TOOLTIP','\nThis Attribute will be Enabled, if the following CONDITIONS are met : \n{0}');
insert into T_MESSAGES(GROUP_NAME,NAME,MESSAGE_TEXT) values ('PIDC_EDITOR','USED_DEPENDENT_ATTR_TOOLTIP','\nThis Attribute will enable following attributes, If the USED-Flag is set to YES : \n{0}');
insert into T_MESSAGES(GROUP_NAME,NAME,MESSAGE_TEXT) values ('PIDC_EDITOR','VAL_DEPENDENT_ATTR_TOOLTIP','\nThis Attribute will enable following attributes, If a specific VALUE is set : \n{0}');
insert into T_MESSAGES(GROUP_NAME,NAME,MESSAGE_TEXT) values ('PIDC_EDITOR','ATTR_MANDATORY','This attribute is Mandatory');
insert into T_MESSAGES(GROUP_NAME,NAME,MESSAGE_TEXT) values ('PIDC_EDITOR','ATTR_DEPENDENT','This is an Dependent attribute');
insert into T_MESSAGES(GROUP_NAME,NAME,MESSAGE_TEXT) values ('PIDC_EDITOR','ATTR_INVISIBLE','This attribute is currently Not Enabled');
insert into T_MESSAGES(GROUP_NAME,NAME,MESSAGE_TEXT) values ('PIDC_EDITOR','ATTR_HYPERLINKS','This attribute has Hyperlink(s)');
insert into T_MESSAGES(GROUP_NAME,NAME,MESSAGE_TEXT) values ('PIDC_EDITOR','ATTR_ALSO_HYPERLINKS',' and has Hyperlink(s)');
commit;

--------------------------------------------------------
--- iCDM-1306 - Additional links in help menu
--------------------------------------------------------

  
insert into tabv_common_params(param_id,param_desc,param_value,version) values ('ICDM_COMMUNITY_LINK','Link to iCDM Community page','https://connect.bosch.com/communities/service/html/communitystart?communityUuid=be26dc6c-c941-45ad-9708-8bb14f5b8c0e');
insert into tabv_common_params(param_id,param_desc,param_value,version) values ('ICDM_WIKI_LINK','Link to iCDM Wiki page','https://inside-docupedia.bosch.com/confluence/display/dsapplication/iCDM+-+intelligent+Calibration+Data+Management');
insert into tabv_common_params(param_id,param_desc,param_value,version) values ('ICDM_CONTACTS_LINK','Links to iCDM Contacts page','https://connect.bosch.com/wikis/home?lang=de-de#!/wiki/Wab093374985e_4fab_9f51_c4ad6125bf82/page/Contact');
commit;

--------------------------------------------------------
--- iCDM-1380 - Assign parameters to Rule set
--------------------------------------------------------
Insert into TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE,VERSION) values ('DB_SEARCH_MAX_RESULT_SIZE','Maximum fetch size of database fetches','100','1');
commit;

-------------------------------------------------------------------

delete from TABV_COMMON_PARAMS WHERE PARAM_ID = 'iCDM_CLIENT_VERSION';
Insert into TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE,VERSION) values ('iCDM_CLIENT_VERSION','iCDM Client''s current version','1.19.0',1);
COMMIT;

spool off