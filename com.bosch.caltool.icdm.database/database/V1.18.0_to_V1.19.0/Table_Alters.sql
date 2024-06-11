spool c:\temp\table_alters.log

--------------------------------------------------------
--  2015-04-02
-------------------------------------------------------- 

--------------------------------------------------------
--- ICDM-1344 - Data model changes in iCDM related to 
--- VCDM info storage in TABV_PROJECTIDCARD
--------------------------------------------------------
----------------------------------------------------------------------
--TABV_PROJECTIDCARD--
----------------------------------------------------------------------
ALTER TABLE TABV_PROJECTIDCARD
  ADD (APRJ_ID NUMBER(15,0),
       VCDM_TRANSFER_USER VARCHAR2(30 BYTE),
       VCDM_TRANSFER_DATE TIMESTAMP(6));   


--------------------------------------------------------
--- iCDM-1306 - Additional links in help menu
-- Increase size of the columns
--------------------------------------------------------

ALTER TABLE tabv_common_params  
 MODIFY 
 ( param_desc varchar2(500),
  param_value varchar2(2000));

--------------------------------------------------------
-- T_RULE_SET
--
-- New table for storing rule set of PIDC 
-- 
-- ICDM-1364
--
CREATE TABLE T_RULE_SET( 
   RSET_ID NUMBER (15) Primary Key,
   RSET_NAME VARCHAR2(100) NOT NULL,
   DESC_ENG VARCHAR2(500) NOT NULL,
   DESC_GER VARCHAR2(500),
   SSD_NODE_ID NUMBER(15) NULL,
   DELETED_FLAG VARCHAR(1) DEFAULT 'N' NOT NULL ,
   CREATED_DATE  TIMESTAMP(6) not null,
   CREATED_USER  VARCHAR2(30) not null,
   MODIFIED_DATE  TIMESTAMP(6),
   MODIFIED_USER  VARCHAR2(30),
   VERSION NUMBER not null, 
   
    CONSTRAINT T_RULE_SET_UK1 UNIQUE (RSET_NAME),
    CONSTRAINT T_RULE_SET_UK2 UNIQUE (SSD_NODE_ID)
 );

--------------------------------------------------------
-- T_RULE_SET_PARAMS
--
-- New table for storing rule set params 
-- 
-- ICDM-1364
--

 CREATE TABLE T_RULE_SET_PARAMS ( 
   RSET_PARAM_ID NUMBER (15) Primary Key,
   RSET_ID NUMBER (15)  NOT NULL,
   PARAM_ID NUMBER (15)  NOT NULL,
   FUNC_ID NUMBER (15) NOT NULL,
   CREATED_DATE  TIMESTAMP(6) not null,
   CREATED_USER  VARCHAR2(30) not null,
   MODIFIED_DATE  TIMESTAMP(6),
   MODIFIED_USER  VARCHAR2(30),
   VERSION NUMBER not null, 

    CONSTRAINT T_RULE_SET_PARAMS_FK1 FOREIGN KEY (RSET_ID) REFERENCES T_RULE_SET (RSET_ID),
    CONSTRAINT T_RULE_SET_PARAMS_FK2 FOREIGN KEY (PARAM_ID) REFERENCES T_PARAMETER (ID),
    CONSTRAINT T_RULE_SET_PARAMS_FK3 FOREIGN KEY (FUNC_ID) REFERENCES T_FUNCTIONS (ID),
    
    CONSTRAINT T_RULE_SET_PARAMS_UK1 UNIQUE (RSET_ID, PARAM_ID)
 );

--------------------------------------------------------
-- T_RULE_SET_PARAM_ATTR
--
-- New table for storing rule set param attrs 
-- 
-- ICDM-1364
--

CREATE TABLE T_RULE_SET_PARAM_ATTR ( 
   RSET_PAR_ATTR_ID NUMBER (15) Primary Key,
   RSET_PARAM_ID NUMBER (15)  NOT NULL,
   ATTR_ID NUMBER (15)  NOT NULL,
   CREATED_DATE  TIMESTAMP(6) not null,
   CREATED_USER  VARCHAR2(30) not null,
   MODIFIED_DATE  TIMESTAMP(6),
   MODIFIED_USER  VARCHAR2(30),
   VERSION NUMBER not null, 

   CONSTRAINT T_RULE_SET_PARAM_ATTR_FK1 FOREIGN KEY (ATTR_ID) REFERENCES TABV_ATTRIBUTES (ATTR_ID),
   CONSTRAINT T_RULE_SET_PARAM_ATTR_FK2 FOREIGN KEY (RSET_PARAM_ID) REFERENCES T_RULE_SET_PARAMS (RSET_PARAM_ID),
   
   CONSTRAINT T_RULE_SET_PARAM_ATTR_UK1 UNIQUE (RSET_PARAM_ID, ATTR_ID)
);

  
  
------------------------------------------------------------
spool off



