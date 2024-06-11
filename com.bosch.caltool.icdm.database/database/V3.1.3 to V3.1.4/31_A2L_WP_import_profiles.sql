spool c:\temp\31_A2L_WP_import_profiles.log

------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 517702: Insert values into T_A2L_WP_IMPORT_PROFILE table  
------------------------------------------------------------------------------------------------------------------

-- P.A.L. Excel File (Function based)
INSERT INTO T_A2L_WP_IMPORT_PROFILE (PROFILE_NAME,PROFILE_ORDER,PROFILE_DETAILS) VALUES ('P.A.L. Excel File (Function based)',2,'{"headingRowNum":2,"sheetName":"PAL","importMode":"F","wpColumn":"workpackage","respColumn":"responsibilities","respTypeColumn":"Responsibility Type","labelColumn":"label name","prefixForWp":"_WP__","prefixForResp":"_RESP__","fileType":{"seperator":"","format":"xlsx,xls,xlsm","type":"Excel"}}');

-- P.A.L. Excel File (Parameter based)
INSERT INTO T_A2L_WP_IMPORT_PROFILE (PROFILE_NAME,PROFILE_ORDER,PROFILE_DETAILS) VALUES ('P.A.L. Excel File (Parameter based)',1,'{"headingRowNum":2,"sheetName":"PAL","importMode":"P","wpColumn":"workpackage","respColumn":"responsibilities","respTypeColumn":"Responsibility Type","labelColumn":"label name","prefixForWp":"_WP__","prefixForResp":"_RESP__","fileType":{"seperator":"","format":"xlsx,xls,xlsm","type":"Excel"}}');

-- iCDM Excel Export of Par2WP
INSERT INTO T_A2L_WP_IMPORT_PROFILE (PROFILE_NAME,PROFILE_ORDER,PROFILE_DETAILS) VALUES ('iCDM Excel Export of Par2WP',3,'{"headingRowNum":1,"sheetName":"PAL","importMode":"P","wpColumn":"WorkPackage","respColumn":"Responsibilities","respTypeColumn":"Responsibility Type","labelColumn":"Parameter Name","prefixForWp":"","prefixForResp":"","fileType":{"seperator":"","format":"xlsx,xls,xlsm","type":"Excel"}}');

-- Generic Excel File
INSERT INTO T_A2L_WP_IMPORT_PROFILE (PROFILE_NAME,PROFILE_ORDER,PROFILE_DETAILS) VALUES ('Generic Excel File',4,'{"headingRowNum":1,"sheetName":"","importMode":"P","wpColumn":"","respColumn":"","respTypeColumn":"","labelColumn":"","prefixForWp":"","prefixForResp":"","fileType":{"seperator":"","format":"xlsx,xls,xlsm","type":"Excel"}}');

COMMIT;

------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 521682: Import Work package - responsibilty from Excel file' button should also allow CSV files - task    
------------------------------------------------------------------------------------------------------------------
-- Creta Report
INSERT INTO T_A2L_WP_IMPORT_PROFILE (PROFILE_NAME,PROFILE_ORDER,PROFILE_DETAILS) VALUES ('Creta Report',5,'{"headingRowNum":9,"sheetName":"","importMode":"P","wpColumn":"Sub Work Package Name","respColumn":"Owner","respTypeColumn":"","labelColumn":"Name","prefixForWp":"_WP__","prefixForResp":"_RESP__","fileType":{"seperator":";","format":"","type":"csv"}}');

COMMIT;


spool off
