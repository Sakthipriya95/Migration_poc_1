spool c:\temp\table_data.log

--------------------------------------------------------
--  2015-02-20
-------------------------------------------------------- 

--------------------------------------------------------
--- ICDM-1272 - Data model changes in iCDM related to Storing of tool tips
--------------------------------------------------------
-- To be executed after the Trigger Statements

insert into T_MESSAGES(GROUP_NAME,NAME,MESSAGE_TEXT) values ('PIDC_EDITOR','CLEARED_VAL_FILTER','Attributes with CLEARED Values');
insert into T_MESSAGES(GROUP_NAME,NAME,MESSAGE_TEXT) values ('PIDC_EDITOR','NOT_CLEARED_VAL_FILTER','Attributes with NOT CLEARED Values');
insert into T_MESSAGES(GROUP_NAME,NAME,MESSAGE_TEXT) values ('PIDC_EDITOR','MISSING_DEP_TOOLBAR_FILTER','All Attributes (also attributes with missing dependencies)');
insert into T_MESSAGES(GROUP_NAME,NAME,MESSAGE_TEXT) values ('PIDC_STRUCTURE','PIDC_STRUCTURE','Structure of active Project ID Card with Virtual Nodes, Variants and Sub-Variants');
insert into T_MESSAGES(GROUP_NAME,NAME,MESSAGE_TEXT) values ('PIDC_TREE','PID_CARD_TAB','Project ID Card Explorer');
insert into T_MESSAGES(GROUP_NAME,NAME,MESSAGE_TEXT) values ('USE_CASE','ATTR_NAME','Attribute Name : {0}\nSuper Group :{1} \nGroup : {2}');
commit;

delete from TABV_COMMON_PARAMS WHERE PARAM_ID = 'iCDM_CLIENT_VERSION';
Insert into TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE,VERSION) values ('iCDM_CLIENT_VERSION','iCDM Client''s current version','1.18.0',1);
COMMIT;

spool off