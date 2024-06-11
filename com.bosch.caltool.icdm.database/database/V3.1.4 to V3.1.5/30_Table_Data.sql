spool c:\temp\01_Table_Data.log


------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 542273: Store and Fetch vCDM Studio path from DB 
------------------------------------------------------------------------------------------------------------------
INSERT INTO TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE) 
VALUES 
  (
    'VCDMSTUDIO_EXE_REL_PATH',
    'Relative path to vCDMStudio executable (multiple records separated by semicolon(;))',
    'Vector vCDM 6.8 SP1\vCDMstudio\Exec\vCDMStudio4vCDM32.exe;Vector vCDMStudio 17.2\Exec\CDMStudio4vCDM32.exe'
  );
COMMIT;

------------------------------------------------------------------------------------------------------------------
-- ALM TaskId : 547471 : Version update to 3.1.5 script
------------------------------------------------------------------------------------------------------------------      
delete from TABV_COMMON_PARAMS WHERE upper(PARAM_ID) = 'ICDM_CLIENT_VERSION';
Insert into TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE,VERSION) values ('ICDM_CLIENT_VERSION','iCDM Client''s current version','3.1.5',1); 
COMMIT;
spool off
