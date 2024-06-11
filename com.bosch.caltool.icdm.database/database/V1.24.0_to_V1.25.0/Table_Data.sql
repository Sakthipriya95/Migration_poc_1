spool c:\temp\table_data.log
---------------------------------------------------------------
----ICDM-2207
---------------------------------------------------------------
---------------------------------------------------------------
-- TABV_COMMON_PARAMS
---------------------------------------------------------------

--Insert new Value in TABV_COMMON_PARAMS for ICDM v1.25.0

delete from TABV_COMMON_PARAMS WHERE PARAM_ID = 'iCDM_CLIENT_VERSION';
Insert into TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE,VERSION) values ('iCDM_CLIENT_VERSION','iCDM Client''s current version','1.25.0',1);
commit;

spool off
