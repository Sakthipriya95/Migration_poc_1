spool c:\temp\30_Table_Data.log

 ------------------------------------------------------------------------------------------------------------------
--Task 664672: Version update to 2022.7.0 script 
------------------------------------------------------------------------------------------------------------------
UPDATE TABV_COMMON_PARAMS SET PARAM_VALUE = '2022.7.0' WHERE PARAM_ID='ICDM_CLIENT_VERSION';
COMMIT;


spool off