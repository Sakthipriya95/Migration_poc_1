spool c:\temp\table_updates.log

--------------------------------------------------------
--  August-11-2014
-------------------------------------------------------- 

------------------------------------------
-- New top level entity in TABV_TOP_LEVEL_ENTITIES for component package
-- ICDM-933
------------------------------------------
INSERT INTO TABV_TOP_LEVEL_ENTITIES(ENT_ID,ENTITY_NAME) values(4,'COMP_PCKG');
/


------------------------------------------
-- Internal Flags for Attributes 
-- ICDM-480
------------------------------------------
ALTER TABLE TABV_ATTRIBUTES ADD attr_security varchar2(1);
/  
update TABV_ATTRIBUTES set attr_security='E';
/  
ALTER TABLE TABV_ATTRIBUTES ADD value_security varchar2(1);
/   
update TABV_ATTRIBUTES set value_security='E';
/ 
ALTER TABLE TABV_ATTRIBUTES   MODIFY attr_security varchar2(1) not null;
/
ALTER TABLE TABV_ATTRIBUTES   MODIFY value_security varchar2(1) not null;
/
commit;

---------------------------------------------------------------------
-- New tables for Characteristics and Characteristics Values for the Attributes 
-- ICDM-954
-------------------------------------------------------------------

CREATE TABLE T_CHARACTERISTICS(
   CHAR_ID NUMBER(15) Primary Key,
   CHAR_NAME_ENG VARCHAR2(100 BYTE)   NOT NULL,
   CHAR_NAME_GER VARCHAR2(100 BYTE),
   DESC_ENG VARCHAR2(100 BYTE),
   DESC_GER VARCHAR2(100 BYTE),
   CREATED_DATE  TIMESTAMP(6) not null,
   CREATED_USER  VARCHAR2(100 BYTE) not null,
   MODIFIED_DATE  TIMESTAMP(6),
   MODIFIED_USER  VARCHAR2(100 BYTE),
   VERSION       NUMBER not null,
    CONSTRAINT T_CHAR_UNIQ UNIQUE (CHAR_NAME_ENG)
);
/

CREATE TABLE T_CHARACTERISTIC_VALUES(
    CHAR_VAL_ID NUMBER(15) Primary Key,
    CHAR_ID NUMBER(15) ,
    VAL_NAME_ENG VARCHAR2(100 BYTE)   NOT NULL,
    VAL_NAME_GER VARCHAR2(100 BYTE),
    DESC_ENG VARCHAR2(100 BYTE),
    DESC_GER VARCHAR2(100 BYTE),
    CREATED_DATE  TIMESTAMP(6) not null,
    CREATED_USER  VARCHAR2(100 BYTE),
    MODIFIED_DATE  TIMESTAMP(6),
    MODIFIED_USER  VARCHAR2(100 BYTE),
    VERSION       NUMBER not null,
    CONSTRAINT T_CHAR_VAL_UNIQ UNIQUE (VAL_NAME_ENG,CHAR_ID)
);
/
ALTER TABLE T_CHARACTERISTIC_VALUES
ADD CONSTRAINT TABV_CHAR_VALUES_FK_C
  FOREIGN KEY (CHAR_ID)
  REFERENCES T_CHARACTERISTICS(CHAR_ID);
/
ALTER TABLE TABV_ATTRIBUTES ADD CHAR_ID NUMBER(15);
/
ALTER TABLE TABV_ATTRIBUTES
ADD CONSTRAINT TABV_CHAR_FK_V
  FOREIGN KEY (CHAR_ID)
  REFERENCES T_CHARACTERISTICS(CHAR_ID);
/
ALTER TABLE TABV_ATTR_VALUES ADD CHAR_VAL_ID NUMBER(15);
/
ALTER TABLE TABV_ATTR_VALUES
ADD CONSTRAINT TABV_CHAR_VALUES_FK_V
  FOREIGN KEY (CHAR_VAL_ID)
  REFERENCES T_CHARACTERISTIC_VALUES(CHAR_VAL_ID);
/

--------------------------------------------------------------------------------
--iCDM-946 : Common param to enable/disable the mail notifications from iCDM
--------------------------------------------------------------------------------
delete from TABV_COMMON_PARAMS WHERE PARAM_ID = 'MAIL_NOTIFICATION_ENABLED';
Insert into TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE,VERSION) values ('MAIL_NOTIFICATION_ENABLED','Status of Automatic Mail Notifications from iCDM','Y',1);
commit;


--------------------------------------------------------
--  Inserts SSD_COMP_PKG_NODE_ID into TABV_COMMON_PARAMS table.
--------------------------------------------------------      
delete from TABV_COMMON_PARAMS WHERE PARAM_ID = 'SSD_COMP_PKG_NODE_ID';
Insert into TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE,VERSION) values ('SSD_COMP_PKG_NODE_ID','SSD NodeId for getting Component Package Data','16770512',1);
COMMIT;

---------------------------------------------------------------------------------------------

delete from TABV_COMMON_PARAMS WHERE PARAM_ID = 'iCDM_CLIENT_VERSION';
Insert into TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE,VERSION) values ('iCDM_CLIENT_VERSION','iCDM Client''s current version','1.13.0',1);
COMMIT;



spool off