spool c:\temp\table_updates.log

--------------------------------------------------------
--  Mar-17-2014
-------------------------------------------------------- 

------------------------------------------
-- Alter T_RVW_PARAMETERS to add Match Ref value exactly flag
-- ICDM-639
------------------------------------------
      
ALTER TABLE T_RVW_PARAMETERS
 ADD MATCH_REF_FLAG VARCHAR2(1);
 
------------------------------------------
-- Alter T_RVW_RESULTS to add Description column
-- ICDM-658
------------------------------------------
      
ALTER TABLE T_RVW_RESULTS ADD DESCRIPTION VARCHAR2(200);

 --------------------------------------------------------
--  Mar-25-2014
-------------------------------------------------------- 

------------------------------------------
-- New table TABV_PIDC_CHANGE_HISTORY to maintain all changes in the PIDC
-- ICDM-627
------------------------------------------

  CREATE TABLE T_PIDC_CHANGE_HISTORY
   ( 
    ID NUMBER PRIMARY KEY,
    PIDC_ID NUMBER NOT NULL, 
    VAR_ID NUMBER,
    SVAR_ID NUMBER,
    ATTR_ID NUMBER,
    PRO_REV_ID NUMBER NOT NULL,
    CHANGED_DATE DATE NOT NULL,
    CHANGED_USER VARCHAR2(30) NOT NULL, 
    PIDC_VERSION NUMBER NOT NULL,
    OLD_VALUE_ID NUMBER,
    NEW_VALUE_ID NUMBER,
    OLD_USED VARCHAR2(1),
    NEW_USED VARCHAR2(1),
    OLD_PART_NUMBER VARCHAR2(20),
    NEW_PART_NUMBER VARCHAR2(20),
    OLD_SPEC_LINK VARCHAR2(1000),
    NEW_SPEC_LINK VARCHAR2(1000),
    OLD_DESCRIPTION VARCHAR2(2000),
    NEW_DESCRIPTION VARCHAR2(2000),
    OLD_DELETED_FLAG VARCHAR2(1),
    NEW_DELETED_FLAG VARCHAR2(1),
    OLD_STATUS_ID NUMBER,
    NEW_STATUS_ID NUMBER,
    OLD_IS_VARIANT VARCHAR2(1),
    NEW_IS_VARIANT VARCHAR2(1),
    VERSION NUMBER DEFAULT 1 NOT NULL
);

--------------------------------------------------------
--  Version update script
--------------------------------------------------------      
delete from TABV_COMMON_PARAMS WHERE PARAM_ID = 'iCDM_CLIENT_VERSION';
Insert into TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE,VERSION) values ('iCDM_CLIENT_VERSION','iCDM Client''s current version','1.8.0',1);
COMMIT;

spool off