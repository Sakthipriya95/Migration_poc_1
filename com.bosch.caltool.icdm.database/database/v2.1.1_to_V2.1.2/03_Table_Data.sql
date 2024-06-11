spool c:\temp\03_Table_Data.log

INSERT INTO TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE,VERSION) 
VALUES ('COMPLI_CLASS_TYPE','The compliance ssd classes of TParameter table in comma seperated values','COMPLIANCE',1);
COMMIT;

spool off