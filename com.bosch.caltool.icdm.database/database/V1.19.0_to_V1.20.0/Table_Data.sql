spool c:\temp\table_data.log

--------------------------------------------------------
--  2015-05-21
-------------------------------------------------------- 

--------------------------------------------------------
--- iCDM-1420 - Access rights hint for cdr editor
--------------------------------------------------------

insert into T_MESSAGES(GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER) values ('RULE_EDITOR','ACCESS_RIGHT_HINT','If you need to assign access rights to multiple {0}, please get in contact with the hotline to support','If you need to assign access rights to multiple {0}, please get in contact with the hotline to support');

update TABV_PROJECTIDCARD
   set APRJ_ID = null
 where APRJ_ID is not null
   and VCDM_TRANSFER_DATE is null
;   

delete from TABV_COMMON_PARAMS WHERE PARAM_ID = 'iCDM_CLIENT_VERSION';
Insert into TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE,VERSION) values ('iCDM_CLIENT_VERSION','iCDM Client''s current version','1.20.0',1);
commit;

spool off