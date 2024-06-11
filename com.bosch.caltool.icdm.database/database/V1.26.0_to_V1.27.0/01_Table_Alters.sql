spool c:\temp\table_alters.log

--------------------------------------------------------
-- T_FOCUS_MATRIX_REVIEW
--
-- New table for Focus Matrix Review details
-- 
-- ICDM-2249
--------------------------------------------------------
CREATE TABLE T_FOCUS_MATRIX_REVIEW
  (
    RVW_ID     NUMBER(15) NOT NULL,
    PIDC_VERS_ID NUMBER(15) NOT NULL,
    RVWED_BY   NUMBER(15),
    RVWED_ON   TIMESTAMP(6)  ,
    LINK       VARCHAR2(1000),
    REMARK       VARCHAR2(4000 BYTE),
    RVW_STATUS    VARCHAR2(1 BYTE),
    CREATED_USER   VARCHAR2(30 BYTE)  NOT NULL,
    CREATED_DATE   TIMESTAMP(6) NOT NULL,
    MODIFIED_DATE  TIMESTAMP(6),
    MODIFIED_USER  VARCHAR2(30 BYTE),
    VERSION        NUMBER NOT NULL,  
  
  CONSTRAINT T_FOCUS_MATRIX_REVIEW_PK PRIMARY KEY (RVW_ID),
  CONSTRAINT T_FOCUS_MATRIX_REVIEW_FK1 FOREIGN KEY (PIDC_VERS_ID) REFERENCES T_PIDC_VERSION (PIDC_VERS_ID),
  CONSTRAINT T_FOCUS_MATRIX_REVIEW_FK2 FOREIGN KEY (RVWED_BY) REFERENCES TABV_APIC_USERS(USER_ID)
  ) ;

 --------------------------------------------------------
-- T_WORKPACKAGE
--
-- New table for work package details
-- 
-- ICDM-2376
-------------------------------------------------------- 
 CREATE TABLE T_WORKPACKAGE
  (
    WP_ID     NUMBER (15) Primary Key ,
    WP_GROUP      VARCHAR2 (100 BYTE) NOT NULL,
    WP_NAME_E      VARCHAR2 (255 BYTE) NOT NULL,
    WP_NAME_G      VARCHAR2 (255 BYTE) ,
    DEFAULT_WP     VARCHAR2 (1 BYTE),
    CREATED_USER  VARCHAR2 (100 BYTE) NOT NULL,
    MODIFIED_USER VARCHAR2 (100 BYTE) ,
    CREATED_DATE  TIMESTAMP NOT NULL ,
    MODIFIED_DATE TIMESTAMP ,
    VERSION       NUMBER NOT NULL,
    
    CONSTRAINT T_WORKPACKAGE_UK1 UNIQUE (WP_GROUP, WP_NAME_E)
    
  ) ;
  
--------------------------------------------------------
-- T_WORKPACKAGE_DIVISION
--
-- New table for workpackage division details
-- 
-- ICDM-2376
--------------------------------------------------------  
  CREATE TABLE T_WORKPACKAGE_DIVISION
  (
    WP_DIV_ID     NUMBER (15) Primary Key ,
    VALUE_ID      NUMBER(15,0) NOT NULL,
    WP_ID          NUMBER (15) NOT NULL,
    CREATED_USER  VARCHAR2 (100 BYTE) NOT NULL,
    MODIFIED_USER VARCHAR2 (100 BYTE) ,
    CREATED_DATE  TIMESTAMP NOT NULL ,
    MODIFIED_DATE TIMESTAMP ,
    VERSION       NUMBER NOT NULL,
    
    CONSTRAINT T_WORKPACKAGE_DIVISION_FK_1 FOREIGN KEY ( WP_ID ) REFERENCES T_WORKPACKAGE ( WP_ID ),
    CONSTRAINT T_WORKPACKAGE_DIVISION_FK_2 FOREIGN KEY ( VALUE_ID ) REFERENCES TABV_ATTR_VALUES ( VALUE_ID ),
    
    CONSTRAINT T_WORKPACKAGE_DIVISION_UK1 UNIQUE (VALUE_ID, WP_ID)
  ) ;
  

---------------------------------------------------------------
----ICDM-2382
---------------------------------------------------------------
---------------------------------------------------------------
-- T_WS_SYSTEMS new table for web service systems 
---------------------------------------------------------------

---------------------------------------------------------------
----ICDM-2382 new table for systems
----------------------- ----------------------------------------

CREATE TABLE T_WS_SYSTEMS
(
SYSTEM_ID NUMBER (15) Primary key,
SYSTEM_TYPE varchar2(100) NOT NULL,
SYSTEM_TOKEN varchar2(200) NOT NULL,
VERSION       NUMBER not null,
  CONSTRAINT T_SYS_UK1 UNIQUE ( SYSTEM_TYPE ),
    CONSTRAINT T_SYS_UK2 UNIQUE ( SYSTEM_TOKEN)
);


-------------------------------------------------------- 
-- ICDM-2376
-------------------------------------------------------- 
alter table T_QUESTIONNAIRE ADD (WP_DIV_ID NUMBER(15));


ALTER TABLE T_QUESTIONNAIRE ADD CONSTRAINT T_QUESTIONNAIRE_FK1
	FOREIGN KEY (WP_DIV_ID) REFERENCES T_WORKPACKAGE_DIVISION(WP_DIV_ID);


-------------------------------------------------------- 
-- ICDM-2438
-------------------------------------------------------- 
ALTER TABLE T_PARAMETER ADD SSD_CLASS varchar2(30) DEFAULT 'NOT_IN_SSD';

alter table t_rvw_parameters add compli_result VARCHAR2(1);
  
alter table t_rvw_parameters add LAB_OBJ_ID NUMBER;
    
alter table t_rvw_parameters add REV_ID NUMBER;
  
alter table t_rvw_parameters add COMPLI_LAB_OBJ_ID NUMBER;
    
alter table t_rvw_parameters add COMPLI_REV_ID NUMBER;

-------------------------------------------------------- 
-- ICDM-2361
--------------------------------------------------------   

ALTER TABLE T_QUESTIONNAIRE
    DROP CONSTRAINT T_QNAIRE_NAME_ENG_UN;

ALTER TABLE T_QUESTIONNAIRE
    ADD CONSTRAINT T_QUESTIONNAIRE_UK1 UNIQUE (NAME_ENG, WP_DIV_ID);

spool off
