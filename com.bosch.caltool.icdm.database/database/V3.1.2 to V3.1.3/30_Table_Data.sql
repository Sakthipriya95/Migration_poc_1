spool c:\temp\30_Table_Data.log

------------------------------------------------------------------------------------------------------------------
-- ALM TaskId : 524922 : Version update script
------------------------------------------------------------------------------------------------------------------      
delete from TABV_COMMON_PARAMS WHERE upper(PARAM_ID) = 'ICDM_CLIENT_VERSION';
Insert into TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE,VERSION) values ('ICDM_CLIENT_VERSION','iCDM Client''s current version','3.1.3',1); 
COMMIT;

spool off

     