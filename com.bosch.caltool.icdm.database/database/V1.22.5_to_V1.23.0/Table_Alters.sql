spool c:\temp\table_updates.log

--------------------------------------------------------
--  January-29-2016  
--------------------------------------------------------

--
-- ICDM-1836
-- T_MANDATORY_ATTR
--
-- New table for storing mandatory attributes list against VALUE_ID of TABV_ATTR_VALUES
-- 
--

  CREATE TABLE T_MANDATORY_ATTR
   (MA_ID NUMBER NOT NULL, 
    DEF_VALUE_ID NUMBER NOT NULL, 
    ATTR_ID NUMBER NOT NULL, 
    CREATED_USER VARCHAR2(30) NOT NULL, 
    CREATED_DATE TIMESTAMP (6) NOT NULL, 
    MODIFIED_DATE TIMESTAMP (6), 
    MODIFIED_USER VARCHAR2(30), 
    VERSION NUMBER NOT NULL, 
    
    CONSTRAINT T_MANDATORY_ATTR_PK PRIMARY KEY (MA_ID), 
    CONSTRAINT  T_MANDATORY_ATTR_UK1 UNIQUE (DEF_VALUE_ID, ATTR_ID),
     
     
    CONSTRAINT  T_MANDATORY_ATTR_FK1 FOREIGN KEY (DEF_VALUE_ID) REFERENCES TABV_ATTR_VALUES (VALUE_ID),
    CONSTRAINT  T_MANDATORY_ATTR_FK2 FOREIGN KEY (ATTR_ID) REFERENCES TABV_ATTRIBUTES (ATTR_ID)
   );

--------------------------------------------------------
--  ICDM-1844 new table for T_ALIAS_DEFINITION
--------------------------------------------------------

    CREATE TABLE T_ALIAS_DEFINITION
   (
    AD_ID NUMBER(15) PRIMARY KEY, 
    AD_NAME VARCHAR2(200) NOT NULL, 
    CREATED_USER VARCHAR2(30) NOT NULL, 
    CREATED_DATE TIMESTAMP (6) NOT NULL, 
    MODIFIED_DATE TIMESTAMP (6), 
    MODIFIED_USER VARCHAR2(30), 
    VERSION NUMBER NOT NULL
   );
   
--------------------------------------------------------
--  ICDM-1844 new table for T_ALIAS_DETAILS
--------------------------------------------------------
   CREATE TABLE T_ALIAS_DETAILS
   (
    ALIAS_DETAILS_ID NUMBER(15) PRIMARY KEY, 
    AD_ID NUMBER NOT NULL, 
    ATTR_ID NUMBER(15) , 
    VALUE_ID NUMBER,
    ALIAS_NAME VARCHAR2(4000) ,
    CREATED_USER VARCHAR2(30) NOT NULL, 
    CREATED_DATE TIMESTAMP (6) NOT NULL, 
    MODIFIED_DATE TIMESTAMP (6), 
    MODIFIED_USER VARCHAR2(30), 
    VERSION NUMBER NOT NULL, 

    CONSTRAINT  T_ALIAS_DET_ATTR_FK1 FOREIGN KEY (VALUE_ID) REFERENCES TABV_ATTR_VALUES (VALUE_ID),
    CONSTRAINT  T_ALIAS_DET_ATTR_FK2 FOREIGN KEY (ATTR_ID) REFERENCES TABV_ATTRIBUTES (ATTR_ID),
    CONSTRAINT  T_ALIAS_DET_ATTR_FK3 FOREIGN KEY (AD_ID) REFERENCES T_ALIAS_DEFINITION (AD_ID),
    CONSTRAINT T_ALIAS_UQ1 CHECK ((VALUE_ID is not null and ALIAS_NAME is not null) or (ATTR_ID is not null and ALIAS_NAME is not null ))
   );
   
----------------------------------------------------------
--  ICDM-1844 new column in TABV_PROJECTIDCARD for alias id
-----------------------------------------------------------
 ALTER TABLE TABV_PROJECTIDCARD ADD (AD_ID NUMBER(15));

----------------------------------------------------------
--  ICDM-1844 Forigen key to T_ALIAS_DEFINITION with alias id
-----------------------------------------------------------
ALTER TABLE TABV_PROJECTIDCARD
ADD CONSTRAINT TABV_PROJECTIDCARD_AD_FK
FOREIGN KEY (AD_ID)
REFERENCES T_ALIAS_DEFINITION(AD_ID);

-------------------------------------------------
--iCDM-1720
--Alter t_rvw_functions to add Function version when a functio is reviewed
----------------------------------------------------------
alter table T_RVW_FUNCTIONS 
add FUNCTION_VERS VARCHAR2(20 BYTE);



-------------------------------------------------
--iCDM-1720
--Alter t_rvw_parameters to add column rvw_file_id to store the file which has the param
----------------------------------------------------------
ALTER TABLE T_RVW_PARAMETERS ADD (RVW_FILE_ID NUMBER);

-------------------------------------------------
--iCDM-1903
--Alter TABV_PROJECT_ATTR to add ATTR_HIDDEN_YN column
----------------------------------------------------------
alter table TABV_PROJECT_ATTR
add ATTR_HIDDEN_YN varchar2(1);

------------------------------------------------------------

ALTER TABLE T_RVW_PARAMETERS
ADD CONSTRAINT T_RVW_PARAMETERS_FK4
FOREIGN KEY (RVW_FILE_ID)
REFERENCES T_RVW_FILES(RVW_FILE_ID);

-------------------------------------------------
--iCDM-1851
--Alter t_parameter to add is bitwise rule flag
----------------------------------------------------------

alter table T_PARAMETER 
add ISBITWISE VARCHAR2(1);

-------------------------------------------------
--iCDM-1895
--Alter t_rvw_parameter to add is bitwise rule flag and bitwise limit col
----------------------------------------------------------

ALTER TABLE T_RVW_PARAMETERS ADD (BITWISE_LIMIT VARCHAR2(400 BYTE));

ALTER TABLE T_RVW_PARAMETERS ADD (ISBITWISE VARCHAR2(1 BYTE));

ALTER TABLE T_RVW_PARAMETERS MODIFY CHANGE_FLAG NUMBER(6,0); 


ALTER TABLE T_FUNCTIONS ADD BIG_FUNCTION VARCHAR2(1) default 'N';

commit;


spool off
