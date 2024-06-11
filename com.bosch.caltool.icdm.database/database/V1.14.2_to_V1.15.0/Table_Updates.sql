spool c:\temp\table_updates.log

--------------------------------------------------------
--  2014-10-15
-------------------------------------------------------- 

------------------------------------------
-- ICDM-1026 new table T_USECASE_FAVORITES
--
--  For private uc, user id is set to USER_ID column
--  For project uc, project id is set to PROJECT_ID column
--  Only one among GROUP_ID, USE_CASE_ID, SECTION_ID should be present
------------------------------------------

CREATE TABLE T_USECASE_FAVORITES(
   UC_FAV_ID NUMBER(15) Primary Key,
   GROUP_ID NUMBER(15),
   USE_CASE_ID NUMBER(15),
   SECTION_ID NUMBER(15),
   USER_ID NUMBER(15),
   PROJECT_ID NUMBER(15),
   CREATED_USER  VARCHAR2(100 BYTE) not null,
   CREATED_DATE  TIMESTAMP(6) not null,
   MODIFIED_USER  VARCHAR2(100 BYTE),
   MODIFIED_DATE  TIMESTAMP(6),
   VERSION       NUMBER not null,
   
    CONSTRAINT T_UC_FAV_FK1 FOREIGN KEY (GROUP_ID) REFERENCES TABV_USE_CASE_GROUPS(GROUP_ID),
    CONSTRAINT T_UC_FAV_FK2 FOREIGN KEY (USE_CASE_ID) REFERENCES TABV_USE_CASES(USE_CASE_ID),
    CONSTRAINT T_UC_FAV_FK3 FOREIGN KEY (SECTION_ID) REFERENCES TABV_USE_CASE_SECTIONS(SECTION_ID),
    CONSTRAINT T_UC_FAV_FK4 FOREIGN KEY (USER_ID) REFERENCES TABV_APIC_USERS(USER_ID),
    CONSTRAINT T_UC_FAV_FK5 FOREIGN KEY (PROJECT_ID) REFERENCES TABV_PROJECTIDCARD(PROJECT_ID),
  
    CONSTRAINT T_UC_FAV_UK1 UNIQUE (USER_ID, PROJECT_ID, GROUP_ID, USE_CASE_ID, SECTION_ID)
    
);
/

CREATE INDEX T_UC_FAV_NDX1 ON T_USECASE_FAVORITES (USER_ID) ;
CREATE INDEX T_UC_FAV_NDX2 ON T_USECASE_FAVORITES (PROJECT_ID);

--------------------------------------------------------
--  2014-10-15
-------------------------------------------------------- 

------------------------------------------
-- ICDM-1032 new table T_PARAM_ATTRS
--
--  Mapping table for paramater and Attribute in iCDM
------------------------------------------
CREATE TABLE T_PARAM_ATTRS(
   PARAM_ATTR_ID NUMBER(15) Primary Key,
   PARAM_ID NUMBER   NOT NULL,
   ATTR_ID NUMBER(15) NOT NULL,
   CREATED_DATE  TIMESTAMP(6) not null,
   CREATED_USER  VARCHAR2(100 BYTE) not null,
   MODIFIED_DATE  TIMESTAMP(6),
   MODIFIED_USER  VARCHAR2(100 BYTE),
   VERSION       NUMBER not null,
   CONSTRAINT T_RULE_UNIQ UNIQUE (PARAM_ID,ATTR_ID),
   CONSTRAINT T_PARAM_ATTR_FK_1 FOREIGN KEY (ATTR_ID) REFERENCES TABV_ATTRIBUTES(ATTR_ID),
   CONSTRAINT T_PARAM_ATTR_FK_2 FOREIGN KEY (PARAM_ID) REFERENCES T_PARAMETER(ID)
);


----------------------------------------------------------------------------
-- 23-10-2015
---------------------------------------------------------------------------- 
------------------------------iCDM-1047-------------------------------------
--------Create unique index to german name to avoid duplicate german names
--**NOTE: Duplicates needs to be removed(if exists) before executing these scripts
---------------------------------------------------------
------- Not Null Unique index on Tabv_Attributes(attr_name_ger)
---------------------------------------------------------
CREATE UNIQUE INDEX TABV_ATTR_IDX1 ON tabv_attributes(
(CASE WHEN attr_name_ger is NOT NULL THEN attr_name_ger END));

---------------------------------------------------------
------- Not Null Unique index on Tabv_Attr_Groups(group_name_ger)
---------------------------------------------------------
CREATE UNIQUE INDEX TABV_ATTR_GRP_IDX1 ON tabv_attr_groups(
(CASE WHEN group_name_ger is NOT NULL THEN group_name_ger END));

---------------------------------------------------------
------- Not Null Unique index on Tabv_Attr_Super_Groups(super_group_name_ger)
---------------------------------------------------------
CREATE UNIQUE INDEX TABV_ATTR_SGRP_IDX1 ON tabv_attr_super_groups(
(CASE WHEN super_group_name_ger is NOT NULL THEN super_group_name_ger END));

---------------------------------------------------------
------- Not Null Unique index on Tabv_Use_Cases(name_ger)
---------------------------------------------------------
CREATE UNIQUE INDEX TABV_UC_IDX1 ON tabv_use_cases(
(CASE WHEN name_ger is NOT NULL THEN name_ger END));

---------------------------------------------------------
------- Not Null Unique index on Tabv_Use_Case_Groups(name_ger)
---------------------------------------------------------
CREATE UNIQUE INDEX TABV_UCG_IDX1 ON tabv_use_case_groups(
(CASE WHEN name_ger is NOT NULL THEN name_ger END));

---------------------------------------------------------
------- Not Null Unique index on Tabv_Use_Case_Sections(name_ger)
---------------------------------------------------------
CREATE UNIQUE INDEX TABV_UCS_IDX1 ON tabv_use_case_sections(
(CASE WHEN name_ger is NOT NULL THEN name_ger END));

---------------------------------------------------------

--ICDM-1043

Insert into TABV_ICDM_FILES (FILE_ID,NODE_ID,NODE_TYPE,FILE_NAME,CREATED_USER,CREATED_DATE,VERSION,FILE_COUNT) 
values (SEQV_ATTRIBUTES.NEXTVAL,-3,'TEMPLATES','Tip_of_the_day.html',user,sysdate,1,1);

Insert into TABV_ICDM_FILE_DATA (FILE_DATA_ID,FILE_ID,FILE_DATA,VERSION) 
SELECT SEQV_ATTRIBUTES.NEXTVAL,FILE_ID,EMPTY_BLOB(),1 FROM TABV_ICDM_FILES WHERE NODE_ID = -3;
--Update BLOB column manually
--The tip of the day  file is available in /com.bosch.caltool.apic.jpa/database/V1.14.0_to_V1.15.0/Files/tipoftheday.html
--The html file along with the resources folder needs to be ZIPPED and uploaded via SQL developer


Insert into TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE,VERSION) 
SELECT 'TIP_OF_THE_DAY_FILE_ID','File id for Tip of the day ',FILE_ID,1 FROM TABV_ICDM_FILES WHERE NODE_ID = -3;

COMMIT;

--ICDM-1055 new Column for Holding the Hint Value
alter table T_PARAMETER add (HINT VARCHAR2(4000));

delete from TABV_COMMON_PARAMS WHERE PARAM_ID = 'iCDM_CLIENT_VERSION';
Insert into TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE,VERSION) values ('iCDM_CLIENT_VERSION','iCDM Client''s current version','1.15.0',1);
COMMIT;

spool off


