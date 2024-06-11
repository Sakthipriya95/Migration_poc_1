spool c:\temp\table_updates.log

--------------------------------------------------------
--  June-13-2014
-------------------------------------------------------- 

------------------------------------------
-- New table GTT_OBJECT_NAMES for storing the Function names and the param names
-- ICDM-801
------------------------------------------

CREATE GLOBAL TEMPORARY TABLE GTT_OBJECT_NAMES
(
  ID        NUMBER PRIMARY KEY,
  OBJ_NAME  VARCHAR2(255 BYTE)
);

------------------------------------------------------------------------
-- Add a new column for TABV_ATTR_VALUES for checking the clearing status
-- ICDM-829
------------------------------------------------------------------------

ALTER TABLE TABV_ATTR_VALUES ADD (clearing_status varchar2(1));

update TABV_ATTR_VALUES set clearing_status='Y';

commit;

ALTER TABLE TABV_ATTR_VALUES
  MODIFY clearing_status varchar2(1) not null;
  
--------------------------------------------------------
-- June-16-2014
-- iCDM-817
-------------------------------------------------------- 
-----------------------------
-- Component Packages --
-- New table to maintain Component packages in iCDM
-----------------------------
CREATE TABLE T_COMP_PKG
(
  COMP_PKG_ID NUMBER PRIMARY KEY,
  COMP_PKG_NAME VARCHAR2(200) NOT NULL,
  DESC_ENG VARCHAR2(2000) NOT NULL,
  DESC_GER VARCHAR2(2000),
  CREATED_USER VARCHAR2(30) NOT NULL,
  CREATED_DATE TIMESTAMP(6) NOT NULL,
  MODIFIED_USER VARCHAR2(30),
  MODIFIED_DATE TIMESTAMP(6),
  DELETED_FLAG VARCHAR2(1) DEFAULT 'N',
  VERSION NUMBER  NOT NULL,

  CONSTRAINT "T_CP_UK1" UNIQUE ("COMP_PKG_NAME")
);

-----------------------------
-- Comp Pakg BC Mapping
-- New table to maintain Component packages and its BC mapping in iCDM
-----------------------------
CREATE TABLE T_COMP_PKG_BC
(
  COMP_BC_ID NUMBER PRIMARY KEY,
  COMP_PKG_ID NUMBER NOT NULL,
  BC_NAME VARCHAR2(200) NOT NULL,  
  BC_SEQ_NO NUMBER NOT NULL,
  CREATED_USER VARCHAR2(30) NOT NULL,
  CREATED_DATE TIMESTAMP(6) NOT NULL,
  MODIFIED_USER VARCHAR2(30),
  MODIFIED_DATE TIMESTAMP(6),
  VERSION NUMBER  NOT NULL,

  CONSTRAINT "T_CP_BC_UK1" UNIQUE ("COMP_PKG_ID","BC_NAME"),
  CONSTRAINT "T_CP_BC_UK2" UNIQUE ("COMP_PKG_ID","BC_SEQ_NO"),
  CONSTRAINT "T_CP_BC_FK1" FOREIGN KEY (COMP_PKG_ID) REFERENCES T_COMP_PKG(COMP_PKG_ID)
);

-----------------------------
-- Comp Pakg BC's FC Mapping
-- New table to maintain Component packages BC and FC mapping in iCDM
-----------------------------
CREATE TABLE T_COMP_PKG_BC_FC
(
  COMP_BC_FC_ID NUMBER PRIMARY KEY,
  COMP_BC_ID NUMBER NOT NULL,
  FC_NAME VARCHAR2(200) NOT NULL,  
  CREATED_USER VARCHAR2(30) NOT NULL,
  CREATED_DATE TIMESTAMP(6) NOT NULL,
  MODIFIED_USER VARCHAR2(30),
  MODIFIED_DATE TIMESTAMP(6),
  VERSION NUMBER  NOT NULL,

  CONSTRAINT "T_CP_BC_FC_UK1" UNIQUE ("COMP_BC_ID","FC_NAME"),
  CONSTRAINT "T_CP_BC_FC_FK1" FOREIGN KEY (COMP_BC_ID) REFERENCES T_COMP_PKG_BC(COMP_BC_ID)
);

--------------------------------------------------------
--  Version update script
--------------------------------------------------------      
delete from TABV_COMMON_PARAMS WHERE PARAM_ID = 'iCDM_CLIENT_VERSION';
Insert into TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE,VERSION) values ('iCDM_CLIENT_VERSION','iCDM Client''s current version','1.11.0',1);

spool off