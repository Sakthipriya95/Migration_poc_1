spool c:\temp\table_updates.log

--------------------------------------------------------
--  August-15-2013  
--------------------------------------------------------

--
-- TABV_USE_CASE_GROUPS
--
-- New table for storing Use Case Groups
-- 
--
ALTER TABLE TABV_UCP_ATTRS DROP CONSTRAINT TABV_UCP_ATTRS_FK3;

ALTER TABLE TABV_UCP_ATTRS DROP CONSTRAINT TABV_UCP_ATTRS_UK1;

ALTER TABLE TABV_USE_CASE_SECTIONS ADD OLD_UCP_ID NUMBER;


Insert into TABV_USE_CASE_SECTIONS (SECTION_ID,USE_CASE_ID,NAME_ENG,NAME_GER,DESC_ENG,DESC_GER,PARENT_SECTION_ID,CREATED_USER,CREATED_DATE,MODIFIED_DATE,MODIFIED_USER,VERSION,DELETED_FLAG, OLD_UCP_ID) 
SELECT SEQV_ATTRIBUTES.NEXTVAL,USE_CASE_ID,NAME_ENG,NAME_GER,DESC_ENG,DESC_GER,SECTION_ID,CREATED_USER,CREATED_DATE,MODIFIED_DATE,MODIFIED_USER,VERSION,DELETED_FLAG, POINT_ID FROM TABV_USE_CASE_POINTS ;

UPDATE TABV_UCP_ATTRS A SET A.SECTION_ID = (SELECT B.SECTION_ID FROM TABV_USE_CASE_SECTIONS B WHERE B.OLD_UCP_ID = A.POINT_ID);

COMMIT;


ALTER TABLE TABV_UCP_ATTRS DROP COLUMN POINT_ID;

ALTER TABLE TABV_UCP_ATTRS MODIFY SECTION_ID NUMBER NULL;

DROP TABLE TABV_USE_CASE_POINTS CASCADE CONSTRAINTS;

ALTER TABLE TABV_UCP_ATTRS ADD CONSTRAINT TABV_UCP_ATTRS_UK1 UNIQUE (USE_CASE_ID, SECTION_ID, ATTR_ID);

ALTER TABLE TABV_USE_CASE_SECTIONS DROP COLUMN OLD_UCP_ID;

--
-- extend textvalue columns due to hyperlinks
--
ALTER TABLE TABV_ATTR_VALUES  
MODIFY (TEXTVALUE_ENG VARCHAR2(2000 BYTE) );

ALTER TABLE TABV_ATTR_VALUES  
MODIFY (TEXTVALUE_GER VARCHAR2(2000 BYTE) );

ALTER TABLE TABV_ATTR_HISTORY  
MODIFY (TEXTVALUE_ENG VARCHAR2(2000 BYTE) );

ALTER TABLE TABV_ATTR_HISTORY  
MODIFY (TEXTVALUE_GER VARCHAR2(2000 BYTE) );

delete from TABV_COMMON_PARAMS WHERE PARAM_ID = 'iCDM_CLIENT_VERSION';
Insert into TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE,VERSION) values ('iCDM_CLIENT_VERSION','iCDM Client''s current version','1.3.0',1);
COMMIT;

spool off

