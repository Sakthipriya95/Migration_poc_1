spool c:\temp\table_alters.log

--------------------------------------------------------
-- T_QUESTIONNAIRE
--
-- New table for storing QUESTIONNAIREs
-- 
-- ICDM-1950
--------------------------------------------------------
CREATE TABLE T_QUESTIONNAIRE
  (
    QNAIRE_ID     NUMBER (15) Primary Key ,
    NAME_ENG      VARCHAR2 (100 BYTE) NOT NULL ,
    NAME_GER      VARCHAR2 (100 BYTE) ,
    DESC_ENG      VARCHAR2 (4000 BYTE) NOT NULL ,
    DESC_GER      VARCHAR2 (4000 BYTE) ,
    CREATED_USER  VARCHAR2 (100 BYTE) NOT NULL ,
    MODIFIED_USER VARCHAR2 (100 BYTE) ,
    CREATED_DATE  TIMESTAMP NOT NULL ,
    MODIFIED_DATE TIMESTAMP ,
    DELETED_FLAG  VARCHAR2 (1 BYTE) ,
    VERSION       NUMBER NOT NULL,
    
    CONSTRAINT T_QNAIRE_NAME_ENG_UN UNIQUE ( NAME_ENG )
  ) ;

--------------------------------------------------------
-- T_QUESTIONNAIRE_VERSION
--
-- New table for storing QUESTIONNAIRE_VERSIONs
-- 
-- ICDM-1950
--------------------------------------------------------
CREATE TABLE T_QUESTIONNAIRE_VERSION
  (
    QNAIRE_VERS_ID            NUMBER (15) Primary Key ,
    QNAIRE_ID                 NUMBER (15) NOT NULL ,
    ACTIVE_FLAG               VARCHAR2 (1 BYTE) ,
    INWORK_FLAG               VARCHAR2 (1 BYTE) ,
    RESULT_RELEVANT_FLAG      VARCHAR2 (1 BYTE) ,
    RESULT_HIDDEN_FLAG        VARCHAR2 (1 BYTE) ,
    MEASUREMENT_RELEVANT_FLAG VARCHAR2 (1 BYTE) ,
    MEASUREMENT_HIDDEN_FLAG   VARCHAR2 (1 BYTE) ,
    SERIES_RELEVANT_FLAG      VARCHAR2 (1 BYTE) ,
    SERIES_HIDDEN_FLAG        VARCHAR2 (1 BYTE) ,
    LINK_RELEVANT_FLAG        VARCHAR2 (1 BYTE) ,
    LINK_HIDDEN_FLAG          VARCHAR2 (1 BYTE) ,
    OPEN_POINTS_RELEVANT_FLAG VARCHAR2 (1 BYTE) ,
    OPEN_POINTS_HIDDEN_FLAG   VARCHAR2 (1 BYTE) ,
    REMARK_RELEVANT_FLAG      VARCHAR2 (1 BYTE) ,
    REMARKS_HIDDEN_FLAG       VARCHAR2 (1 BYTE) ,
    MAJOR_VERSION_NUM         NUMBER NOT NULL ,
    MINOR_VERSION_NUM         NUMBER ,
    DESC_ENG                  VARCHAR2 (4000 BYTE) NOT NULL ,
    DESC_GER                  VARCHAR2 (4000 BYTE) ,
    CREATED_USER              VARCHAR2 (100 BYTE) NOT NULL ,
    MODIFIED_USER             VARCHAR2 (100 BYTE) ,
    CREATED_DATE              TIMESTAMP NOT NULL ,
    MODIFIED_DATE             TIMESTAMP ,
    VERSION                   NUMBER NOT NULL,
    
    CONSTRAINT T_QUESTIONNAIRE_VERSION_UK_1 UNIQUE ( QNAIRE_ID , MAJOR_VERSION_NUM , MINOR_VERSION_NUM ),
    CONSTRAINT T_QUESTIONNAIRE_VERSION_FK_1 FOREIGN KEY ( QNAIRE_ID ) REFERENCES T_QUESTIONNAIRE ( QNAIRE_ID )
  ) ;

--------------------------------------------------------
-- T_QUESTION
--
-- New table for storing QUESTIONs
-- 
-- ICDM-1950
--------------------------------------------------------
CREATE TABLE T_QUESTION
  (
    Q_ID           NUMBER (15) Primary Key ,
    QNAIRE_VERS_ID NUMBER (15) NOT NULL ,
    Q_NUMBER       NUMBER NOT NULL ,
    Q_NAME_ENG     VARCHAR2 (1000 BYTE) NOT NULL ,
    Q_NAME_GER     VARCHAR2 (1000 BYTE) ,
    Q_HINT_ENG     VARCHAR2 (4000 BYTE) NOT NULL ,
    Q_HINT_GER     VARCHAR2 (4000 BYTE) ,
    HEADING_FLAG   VARCHAR2 (1 BYTE) ,
    PARENT_Q_ID    NUMBER (15),
    DELETED_FLAG   VARCHAR2 (1 BYTE) ,
    CREATED_USER   VARCHAR2 (100 BYTE) NOT NULL ,
    MODIFIED_USER  VARCHAR2 (100 BYTE) ,
    CREATED_DATE   TIMESTAMP NOT NULL ,
    MODIFIED_DATE  TIMESTAMP ,
    VERSION        NUMBER NOT NULL,
    
    CONSTRAINT T_QUESTION__UK_1 UNIQUE ( QNAIRE_VERS_ID , PARENT_Q_ID , Q_NUMBER ),
    CONSTRAINT T_QUESTION_FK_1 FOREIGN KEY ( QNAIRE_VERS_ID ) REFERENCES T_QUESTIONNAIRE_VERSION ( QNAIRE_VERS_ID ),
    CONSTRAINT T_QUESTION_FK_2 FOREIGN KEY ( PARENT_Q_ID ) REFERENCES T_QUESTION ( Q_ID )
  ) ;
  
--CREATE INDEX T_QUESTION__IDX_1 ON T_QUESTION
--  ( QNAIRE_VERS_ID ASC
--  ) ;

--------------------------------------------------------
-- T_QUESTION_CONFIG
--
-- New table for storing QUESTION related configurations
-- 
-- ICDM-1950
--------------------------------------------------------
CREATE TABLE T_QUESTION_CONFIG
  (
    QCONFIG_ID  NUMBER (15) Primary Key ,
    Q_ID        NUMBER (15) NOT NULL ,
    RESULT      VARCHAR2 (1 BYTE) ,
    MEASUREMENT VARCHAR2 (1 BYTE) ,
    SERIES      VARCHAR2 (1 BYTE) ,
    LINK        VARCHAR2 (1 BYTE) ,
    OPEN_POINTS VARCHAR2 (1 BYTE) ,
    REMARK        VARCHAR2 (1 BYTE) ,
    CREATED_USER  VARCHAR2 (100 BYTE) NOT NULL ,
    MODIFIED_USER VARCHAR2 (100 BYTE) ,
    CREATED_DATE  TIMESTAMP NOT NULL ,
    MODIFIED_DATE TIMESTAMP ,
    VERSION       NUMBER NOT NULL,
    
    CONSTRAINT T_QUESTION_CONFIG_UK_1 UNIQUE ( Q_ID ),
    CONSTRAINT T_QUESTION_CONFIG_FK_1 FOREIGN KEY ( Q_ID ) REFERENCES T_QUESTION ( Q_ID )
  ) ;

--------------------------------------------------------
-- T_QUESTION_DEPEN_ATTRIBUTES
--
-- New table for storing QUESTION dependency attributes
-- 
-- ICDM-1950
--------------------------------------------------------
CREATE TABLE T_QUESTION_DEPEN_ATTRIBUTES
  (
    QATTR_DEPEN_ID NUMBER (15) Primary Key ,
    Q_ID           NUMBER (15) NOT NULL ,
    ATTR_ID        NUMBER (15) NOT NULL ,
    CREATED_USER   VARCHAR2 (100 BYTE) NOT NULL ,
    MODIFIED_USER  VARCHAR2 (100 BYTE) ,
    CREATED_DATE   TIMESTAMP NOT NULL ,
    MODIFIED_DATE  TIMESTAMP ,
    VERSION        NUMBER NOT NULL,
    
    CONSTRAINT T_QUESTION_DEPEN_ATTRS_UK_1 UNIQUE ( Q_ID , ATTR_ID ),
    CONSTRAINT T_QUESTION_DEPEN_ATTRS_FK_1 FOREIGN KEY ( ATTR_ID ) REFERENCES TABV_ATTRIBUTES ( ATTR_ID ),
    CONSTRAINT T_QUESTION_DEPEN_ATTRS_FK_2 FOREIGN KEY ( Q_ID ) REFERENCES T_QUESTION ( Q_ID )
  ) ;
  
  
--------------------------------------------------------
-- T_QUESTION_DEPEN_ATTR_VALUES
--
-- New table for storing QUESTION dependency attribute values
-- 
-- ICDM-1950
--------------------------------------------------------
CREATE TABLE T_QUESTION_DEPEN_ATTR_VALUES
  (
    DEPEN_ATTR_VAL_ID NUMBER (15) Primary Key ,
    Q_ATTR_DEP_ID     NUMBER (15) NOT NULL ,
    Q_COMBI_NUM       NUMBER(15) NOT NULL,
    VALUE_ID          NUMBER (15) NOT NULL ,
    CREATED_USER      VARCHAR2 (100 BYTE) NOT NULL ,
    MODIFIED_USER     VARCHAR2 (100 BYTE) ,
    CREATED_DATE      TIMESTAMP NOT NULL ,
    MODIFIED_DATE     TIMESTAMP ,
    VERSION           NUMBER NOT NULL,
    
    CONSTRAINT T_Q_DEPEN_ATTR_VALUES_UK_1 UNIQUE ( Q_ATTR_DEP_ID ,Q_COMBI_NUM, VALUE_ID ),
    CONSTRAINT T_Q_DEPEN_ATTR_VALUES_FK_1 FOREIGN KEY ( VALUE_ID ) REFERENCES TABV_ATTR_VALUES ( VALUE_ID ),
    CONSTRAINT T_Q_DEPEN_ATTR_VALUES_FK_2 FOREIGN KEY ( Q_ATTR_DEP_ID ) REFERENCES T_QUESTION_DEPEN_ATTRIBUTES ( QATTR_DEPEN_ID )
    
  ) ;

  
--------------------------------------------------------
-- TABV_PROJECT_ATTR
--
-- changing the column size from DESCRIPTION varchar2(2000) to DESCRIPTION varchar2(4000)
-- 
-- ICDM-2006
--------------------------------------------------------
ALTER TABLE TABV_PROJECT_ATTR MODIFY DESCRIPTION varchar2(4000);
--------------------------------------------------------
-- TABV_VARIANTS_ATTR
--
-- changing the column size from DESCRIPTION varchar2(2000) to DESCRIPTION varchar2(4000)
-- 
-- ICDM-2006
--------------------------------------------------------
ALTER TABLE TABV_VARIANTS_ATTR MODIFY DESCRIPTION varchar2(4000);
  
--------------------------------------------------------
-- TABV_PROJ_SUB_VARIANTS_ATTR
--
-- changing the column size from DESCRIPTION varchar2(2000) to DESCRIPTION varchar2(4000)
-- 
-- ICDM-2006
--------------------------------------------------------
ALTER TABLE TABV_PROJ_SUB_VARIANTS_ATTR MODIFY DESCRIPTION varchar2(4000);

--------------------------------------------------------
-- T_PIDC_CHANGE_HISTORY
-- 
-- ICDM-2006
--------------------------------------------------------
ALTER TABLE T_PIDC_CHANGE_HISTORY MODIFY 
(OLD_SPEC_LINK varchar2(4000), 
NEW_SPEC_LINK varchar2(4000),

OLD_VALUE_DESC_ENG varchar2(4000),
 NEW_VALUE_DESC_ENG varchar2(4000),

OLD_VALUE_DESC_GER varchar2(4000),
 NEW_VALUE_DESC_GER varchar2(4000),
 
 NEW_DESCRIPTION varchar2(4000), 
 OLD_DESCRIPTION varchar2(4000));

 
 --------------------------------------------------------
-- TABV_ATTR_HISTORY
--
-- ICDM-2006
--------------------------------------------------------
  ALTER TABLE TABV_ATTR_HISTORY MODIFY 
  (VALUE_DESC_GER varchar2(4000),
 VALUE_DESC_ENG varchar2(4000),
 DESC_ENG varchar2(4000),
 DESC_GER varchar2(4000));
 
--------------------------------------------------------
-- T_RVW_QUESTIONNAIRES
--
-- New table for storing QUESTIONNAIRE Responses mapped against a review result
-- 
-- ICDM-1979
--------------------------------------------------------
CREATE TABLE T_RVW_QUESTIONNAIRE
  (
    RVW_QNAIRE_ID  NUMBER (15) Primary Key ,
    RESULT_ID      NUMBER (15) NOT NULL ,
    QNAIRE_VERS_ID NUMBER (15) NOT NULL ,
    CREATED_USER   VARCHAR2 (100 BYTE) NOT NULL ,
    MODIFIED_USER  VARCHAR2 (100 BYTE) ,
    CREATED_DATE   TIMESTAMP NOT NULL ,
    MODIFIED_DATE  TIMESTAMP,
    VERSION        NUMBER NOT NULL,
    
    CONSTRAINT T_RVW_QUESTIONNAIRES_UK_1 UNIQUE ( RESULT_ID , QNAIRE_VERS_ID ),
    CONSTRAINT T_RVW_QUESTIONNAIRES_FK1 FOREIGN KEY ( RESULT_ID ) REFERENCES T_RVW_RESULTS ( RESULT_ID ),
    CONSTRAINT T_RVW_QUESTIONNAIRES_FK2 FOREIGN KEY ( QNAIRE_VERS_ID ) REFERENCES T_QUESTIONNAIRE_VERSION ( QNAIRE_VERS_ID )
  ) ;
 
--------------------------------------------------------
-- T_RVW_QNAIRE_ANSWERS
--
-- New table for storing response to Questionnaires 
-- 
-- ICDM-1979
--------------------------------------------------------  
CREATE TABLE T_RVW_QNAIRE_ANSWER
  (
    RVW_ANSWER_ID  NUMBER (15) Primary Key ,
    VARIANT_ID     NUMBER (15) ,
    PIDC_VERS_ID   NUMBER NOT NULL ,
    QNAIRE_VERS_ID NUMBER NOT NULL ,
    Q_ID           NUMBER NOT NULL ,
    RESULT         VARCHAR2 (1 BYTE) ,
    MEASUREMENT    VARCHAR2 (1 BYTE) ,
    SERIES         VARCHAR2 (1 BYTE) ,
    OPEN_POINTS    VARCHAR2 (4000 BYTE) ,
    REMARK         VARCHAR2 (4000 BYTE) ,
    CREATED_USER   VARCHAR2 (100 BYTE) NOT NULL ,
    MODIFIED_USER  VARCHAR2 (100 BYTE) ,
    CREATED_DATE   TIMESTAMP NOT NULL ,
    MODIFIED_DATE  TIMESTAMP ,
    VERSION        NUMBER NOT NULL,
    
    CONSTRAINT T_RVW_QNAIRE_ANSWERS__FK_1 FOREIGN KEY ( VARIANT_ID ) REFERENCES TABV_PROJECT_VARIANTS ( VARIANT_ID ),
    CONSTRAINT T_RVW_QNAIRE_ANSWERS__FK_2 FOREIGN KEY ( PIDC_VERS_ID ) REFERENCES T_PIDC_VERSION ( PIDC_VERS_ID ),
    CONSTRAINT T_RVW_QNAIRE_ANSWERS__FK_3 FOREIGN KEY ( QNAIRE_VERS_ID ) REFERENCES T_QUESTIONNAIRE_VERSION ( QNAIRE_VERS_ID ),
    CONSTRAINT T_RVW_QNAIRE_ANSWERS__FK_4 FOREIGN KEY ( Q_ID ) REFERENCES T_QUESTION ( Q_ID )
    
  ) ;
  
--------------------------------------------------------
-- DDL for creation of new table T_RVW_VARIANTS 
-- 
-- ICDM-2084
--------------------------------------------------------

CREATE TABLE T_RVW_VARIANTS
  (
    RVW_VAR_ID  NUMBER (15) Primary Key ,
    VARIANT_ID  NUMBER (15) NOT NULL,
    RESULT_ID   NUMBER (15) NOT NULL ,
    CREATED_USER              VARCHAR2 (100 BYTE) NOT NULL ,
    MODIFIED_USER             VARCHAR2 (100 BYTE) ,
    CREATED_DATE              TIMESTAMP NOT NULL ,
    MODIFIED_DATE             TIMESTAMP ,
    VERSION                   NUMBER NOT NULL,
    CONSTRAINT T_RVW_VARIANTS_FK_1 FOREIGN KEY ( RESULT_ID ) REFERENCES t_rvw_results ( RESULT_ID ),
    CONSTRAINT T_RVW_VARIANTS_FK_2 FOREIGN KEY ( VARIANT_ID ) REFERENCES TABV_PROJECT_VARIANTS ( VARIANT_ID )
  ) ;
  
alter table T_RVW_VARIANTS add CONSTRAINT  T_RVW_VARIANTS_UK_1 unique (VARIANT_ID,RESULT_ID);

-- Migrate the variant references from T_RVW_RESULTS.VARIANT_ID to the new table T_RVW_VARIANTS
INSERT INTO T_RVW_VARIANTS (VARIANT_ID, RESULT_ID, CREATED_DATE,CREATED_USER)
    ( select VARIANT_ID,RESULT_ID,created_date,created_user from t_rvw_results where variant_id is not null);
    
commit;

-- ICDM-2108
-- Drop column VARIANT_ID from T_RVW_RESULTS if migration is OK.
-- ALTER TABLE T_RVW_RESULTS DROP COLUMN VARIANT_ID CASCADE CONSTRAINTS;


--------------------------------------------------------
--  Extra Index for T_RVW_VARIANTS
--  ICDM-2087
--------------------------------------------------------

CREATE INDEX T_RVW_VARIANTS_IDX1 ON T_RVW_VARIANTS (RESULT_ID);
CREATE INDEX T_RVW_VARIANTS_IDX2 ON T_RVW_VARIANTS (VARIANT_ID);

--------------------------------------------------------
-- T_RVW_RESULTS
--
-- ICDM-2026
--------------------------------------------------------
ALTER TABLE T_RVW_RESULTS MODIFY SOURCE_TYPE VARCHAR2(10) NOT NULL;

spool off
