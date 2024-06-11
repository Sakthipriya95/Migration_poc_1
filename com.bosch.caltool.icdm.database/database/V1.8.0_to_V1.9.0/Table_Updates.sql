spool c:\temp\table_updates.log

--------------------------------------------------------
--  April-28-2014
-------------------------------------------------------- 

------------------------------------------
-- Alter T_PIDC_CHANGE_HISTORY to add additional columns
-- ICDM-688
------------------------------------------
      
ALTER TABLE T_PIDC_CHANGE_HISTORY
 ADD OLD_VALUE_DESC_ENG VARCHAR2(255)
 ADD NEW_VALUE_DESC_ENG VARCHAR2(255)
 ADD OLD_VALUE_DESC_GER VARCHAR2(255)
 ADD NEW_VALUE_DESC_GER VARCHAR2(255)
 ADD OLD_TEXTVALUE_ENG VARCHAR2(2000)
 ADD NEW_TEXTVALUE_ENG VARCHAR2(2000)
 ADD OLD_TEXTVALUE_GER VARCHAR2(2000)
 ADD NEW_TEXTVALUE_GER VARCHAR2(2000);

----------------------------------------------
-- Set the deleted flag to 'Y' for already deleted varaints in TABV_PROJECT_VARIANT
-- ICDM-668
----------------------------------------------

UPDATE TABV_PROJECT_VARIANTS var
SET var.DELETED_FLAG='Y' WHERE var.VALUE_ID in (select val.value_id from TABV_ATTR_VALUES val where val.DELETED_FLAG='Y') ;
commit;

----------------------------------------------
-- Set the deleted flag to 'Y' for already deleted sub-varaints in TABV_PROJECT_SUB_VARIANT
-- ICDM-668
----------------------------------------------

UPDATE TABV_PROJECT_SUB_VARIANTS svar
SET svar.DELETED_FLAG='Y' WHERE svar.VALUE_ID in (select val.value_id from TABV_ATTR_VALUES val where val.DELETED_FLAG='Y') ;
commit;

----------------------------------------------
-- Insert a new row for checking the Maximum param count in TABV_COMMON_PARAMS
-- ICDM-715
----------------------------------------------
insert into TABV_COMMON_PARAMS values ('CDR_MAX_PARAM_COUNT','Maximum number of parameters that can be processed for CDR','1000',1);
commit;


----------------------------------------------
-- Insert a new column in the Review Result table for String the Source Type currently a nullable columnn. Will be changed as not null.
-- ICDM-729
----------------------------------------------

alter table T_RVW_RESULTS add SOURCE_TYPE  varchar2(10) ;
--------------------------------------------------------
--  Version update script
--------------------------------------------------------      
delete from TABV_COMMON_PARAMS WHERE PARAM_ID = 'iCDM_CLIENT_VERSION';
Insert into TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE,VERSION) values ('iCDM_CLIENT_VERSION','iCDM Client''s current version','1.9.0',1);
COMMIT;

spool off