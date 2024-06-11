spool c:\temp\30_Table_Data.log

---------------------------------------------------------------------
--  ALM Task : 648782 - Impl : In 'CDFXExportWizardDialog' the 'Readiness' points which are listed in 'German' has to be translated to 'English'
---------------------------------------------------------------------
--Inserting new TABV_COMMON_PARAMS for  CDFX Export Readines
INSERT INTO TABV_COMMON_PARAMS 
    (PARAM_ID, PARAM_DESC, PARAM_VALUE, VERSION) 
  VALUES ('CDFX_READINESS_FILE_NODE_EN_ID', 'Node id of English file for 100% cdfx export readiness condition', '-10', '1');

--inserting new files for CDFX Export Readiness
INSERT INTO tabv_icdm_files 
    (NODE_ID, NODE_TYPE,FILE_NAME, FILE_COUNT) 
  values (-10, 'TEMPLATES', 'cdfx_readiness_condition_en.html', 1);
    
-- Once the filedata with empty blob is inserted then the file has to be 
-- uploaded from sql developer directly
INSERT INTO TABV_ICDM_FILE_DATA
    (FILE_DATA_ID, FILE_ID, FILE_DATA)
  SELECT SEQV_ATTRIBUTES.NEXTVAL, FILE_ID, EMPTY_BLOB() 
    FROM TABV_ICDM_FILES 
    WHERE NODE_ID = -10;

--updating the tabv_common_params param id to deutsch 
UPDATE TABV_COMMON_PARAMS 
    SET PARAM_ID = 'CDFX_READINESS_FILE_NODE_DE_ID',
        PARAM_DESC = 'Node id of deutsch file for 100% cdfx export readiness condition'
    where PARAM_ID = 'CDFX_READINESS_FILE_NODE_ID';


----------------------------------------------------------------------------
-- ALM Task 635164 - Par2WP Excel Sheet import shows misleading error messages.
--  
-- To update the Dialog box message for the scenario where existing mappings in 
-- the A2L file are equal to the Excel file.  
----------------------------------------------------------------------------
UPDATE  T_MESSAGES 
SET message_text = 'Mappings in Excel file are already equal to mappings in iCDM. There is no need to insert data from Excel to iCDM.'
WHERE GROUP_NAME = 'FILE_IMPORT_TO_A2L_WP' 
  and NAME = 'WP_ALREADY_EXISTS';    


COMMIT;    

 ------------------------------------------------------------------------------------------------------------------
--Version update to 2022.3.0 script 
------------------------------------------------------------------------------------------------------------------
UPDATE TABV_COMMON_PARAMS SET PARAM_VALUE = '2022.3.0' WHERE PARAM_ID='ICDM_CLIENT_VERSION';
COMMIT;


spool off