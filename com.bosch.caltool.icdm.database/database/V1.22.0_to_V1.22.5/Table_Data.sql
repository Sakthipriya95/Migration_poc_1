spool c:\temp\table_data.log
---------------------------------------------------------------

--  2016-1-21
---------------------------------------------------------------

---------------------------------------------------------------
----ICDM-1797
---------------------------------------------------------------
---------------------------------------------------------------
-- TABV_COMMON_PARAMS
---------------------------------------------------------------

--Insert new Value in TABV_COMMON_PARAMS for ICDM v1.22.5

delete from TABV_COMMON_PARAMS WHERE PARAM_ID = 'iCDM_CLIENT_VERSION';
Insert into TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE,VERSION) values ('iCDM_CLIENT_VERSION','iCDM Client''s current version','1.22.5',1);
commit;
spool off