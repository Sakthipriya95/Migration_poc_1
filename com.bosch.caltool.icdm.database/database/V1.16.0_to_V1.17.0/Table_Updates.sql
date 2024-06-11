spool c:\temp\table_updates.log

--------------------------------------------------------
--  2014-12-24
-------------------------------------------------------- 

--------------------------------------------------------
--- iCDM-1144 - Update UNIQUE index for Use-Case tables
--------------------------------------------------------
----------------------------------------------------------------------
--TABV_USE_CASE_SECTIONS--
----------------------------------------------------------------------
alter table TABV_USE_CASE_SECTIONS
 drop constraint TABV_USE_CASE_SECTIONS_UK1;
 
drop index TABV_UCS_IDX1;
 
alter table TABV_USE_CASE_SECTIONS
 add constraint TABV_USE_CASE_SECTIONS_UK1 unique (USE_CASE_ID,NAME_ENG);
 
CREATE UNIQUE INDEX TABV_UCS_IDX1 ON tabv_use_case_sections(USE_CASE_ID,
 (CASE WHEN name_ger is NOT NULL THEN name_ger else name_eng END));

----------------------------------------------------------------------
--TABV_USE_CASE_GROUPS--
----------------------------------------------------------------------

 alter table TABV_USE_CASE_GROUPS
 drop constraint TABV_USE_CASE_GROUPS_UK1;
 
 drop index TABV_UCG_IDX1;
 
 alter table TABV_USE_CASE_GROUPS
  add constraint TABV_USE_CASE_GROUPS_UK1 unique (GROUP_ID,NAME_ENG);

 CREATE UNIQUE INDEX TABV_UCG_IDX1 on TABV_USE_CASE_GROUPS(GROUP_ID,
 (CASE WHEN NAME_GER IS NOT NULL THEN NAME_GER ELSE NAME_ENG END ));
 
-----------------------------------------------------------------------
--TABV_USE_CASES--
-----------------------------------------------------------------------
drop index TABV_UC_IDX1;

create unique index TABV_UC_IDX1 on TABV_USE_CASES(GROUP_ID,
(CASE WHEN NAME_GER IS NOT NULL THEN NAME_GER ELSE NAME_ENG END));
 
------------------------------------------------------------------------ 
------------------------------------------------------------------------
--------------------------------------------------------------------
---- Icdm- 1214  T_RVW_ATTR_VALUES for String the Review Attr Values---
---------------------------------------------------------------------

CREATE TABLE T_RVW_ATTR_VALUES(
   RVW_ATTRVAL_ID NUMBER(15) Primary Key,
   RESULT_ID NUMBER   NOT NULL,
   --ICDM-1238
   ATTR_ID         NUMBER(15)   NOT NULL,    
   VALUE_ID NUMBER(15) ,
   CREATED_DATE  TIMESTAMP(6) not null,
   CREATED_USER  VARCHAR2(100 BYTE) not null,
   MODIFIED_DATE  TIMESTAMP(6),
   MODIFIED_USER  VARCHAR2(100 BYTE),
   VERSION       NUMBER not null,
   CONSTRAINT T_RVW_VAL_UNIQ UNIQUE (RESULT_ID,ATTR_ID),
   CONSTRAINT T_RVW_VAL_FK_1 FOREIGN KEY (RESULT_ID) REFERENCES T_RVW_RESULTS(RESULT_ID),
   CONSTRAINT T_RVW_VAL_FK_2 FOREIGN KEY (VALUE_ID) REFERENCES TABV_ATTR_VALUES(VALUE_ID),
   CONSTRAINT T_RVW_VAL_FK_3 FOREIGN KEY (ATTR_ID)  REFERENCES DGS_ICDM.TABV_ATTRIBUTES (ATTR_ID));


--------------------------------------------------------------------
---- Icdm- 1179  T_FC_GRP_WP_TYPE for FC and Group mapping to Work package
---------------------------------------------------------------------

CREATE TABLE T_FC_GRP_WP_TYPE(
   TYPE_ID NUMBER(15) Primary Key,
   TYPE VARCHAR2(100 BYTE) not null,
   VALUE_ID NUMBER(15) NOT NULL,
   VERSION       NUMBER not null,
   CONSTRAINT T_FC_GRP_TYPE_FK_1 FOREIGN KEY (VALUE_ID) REFERENCES TABV_ATTR_VALUES(VALUE_ID)
);
--------------------------------------------------------------------
---- Icdm- 1179 Insert Script for three new Values for the mapping table
---------------------------------------------------------------------

insert into T_FC_GRP_WP_TYPE (type,value_id) (select 'FC2WP1', param_value from tabv_common_params where param_id='WP_FC2WP1_VALUE_ID' );
insert into T_FC_GRP_WP_TYPE (type,value_id) (select 'FC2WP2', param_value from tabv_common_params where param_id='WP_FC2WP2_VALUE_ID' );
insert into T_FC_GRP_WP_TYPE (type,value_id) (select 'GROUP_MAPPING', param_value from tabv_common_params where param_id='WP_GROUP_VALUE_ID' );
delete from tabv_common_params where param_id in ('WP_FC2WP1_VALUE_ID' ,'WP_FC2WP2_VALUE_ID' ,'WP_GROUP_VALUE_ID' );
commit;
---------------------------------------------------------------------------------------
---- Icdm- 1180  Update Script for Changing the Clearing Status of existing Attr Values
---------------------------------------------------------------------------------------
update tabv_attr_values set CLEARING_STATUS='D' where clearing_status='Y' and DELETED_FLAG='Y';
update tabv_attr_values set CLEARING_STATUS='R' where clearing_status in ('N','I') and DELETED_FLAG='Y';
commit;

---------------------------------------------------------------------------------------
---- ICDM-1242  Update Script for adding the column USED_FLAG to the T_SSD_VALUES table
---------------------------------------------------------------------------------------
ALTER TABLE T_SSD_VALUES
ADD (USED_FLAG VARCHAR2(1 BYTE));
commit;

delete from TABV_COMMON_PARAMS WHERE PARAM_ID = 'iCDM_CLIENT_VERSION';
Insert into TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE,VERSION) values ('iCDM_CLIENT_VERSION','iCDM Client''s current version','1.17.0',1);
COMMIT;

spool off



