spool c:\temp\01_Table_Alters.log

---------------------------------------------------------------------------    
-- ALM Task:649222  - Update CoC WP Used Flag on add/remove of Project use case.   
----------------------------------------------------------------------------
ALTER TABLE TABV_USE_CASE_SECTIONS 
    ADD CONSTRAINT USE_CASE_SEC_FK_WPML_ID FOREIGN KEY (WP_MASTERLIST_ID) REFERENCES T_WPML_WP_MASTERLIST (ID);
            
---------------------------------------------------------------------------    
-- ALM Task:655174  - Add column fc_without_params
--
-- To add a new column FC_WITHOUT_PARAMS in table T_FC2WP_MAPPING.   
----------------------------------------------------------------------------
    
ALTER TABLE T_FC2WP_MAPPING
    ADD (
        FC_WITH_PARAMS_FLAG VARCHAR2(1) DEFAULT 'Y' NOT NULL
    );
    
    
---------------------------------------------------------------------------    
-- ALM Task:655250  - Additional remark field in FC2WP
--
-- To add a new column FCWP_INFO in table T_FC2WP_MAPPING.   
----------------------------------------------------------------------------
    
ALTER TABLE T_FC2WP_MAPPING
    ADD (
        FCWP_INFO VARCHAR2(4000 BYTE) 
    );
    
---------------------------------------------------------------------------------------------------------------
--  ALM Task : 631316 - impl : Store Read-Only and Dependent params in database during review process
--
---------------------------------------------------------------------------------------------------------------
-- Add column READ_ONLY_PARAM to T_RVW_PARAMETERS
ALTER TABLE T_RVW_PARAMETERS ADD READ_ONLY_PARAM VARCHAR2(1) DEFAULT 'N' NOT NULL;

-- To create table to store dependent parameters
create table T_A2L_DEP_PARAMS
    (
        A2L_DEP_PARAM_ID NUMBER NOT NULL,
        A2L_FILE_ID NUMBER NOT NULL, 
        PARAM_NAME VARCHAR2(100) NOT NULL,
        DEPENDS_ON_PARAM_NAME VARCHAR2(255) NOT NULL,
        CREATED_DATE   TIMESTAMP (6) NOT NULL, 
        CREATED_USER   VARCHAR2(30 BYTE) NOT NULL, 
        MODIFIED_DATE  TIMESTAMP (6), 
        MODIFIED_USER  VARCHAR2(30 BYTE), 
        VERSION      NUMBER NOT NULL,
        
        CONSTRAINT A2L_DEP_PARAMS_PK PRIMARY KEY (A2L_DEP_PARAM_ID),
        CONSTRAINT A2L_DEP_PARAMS_UNIQ UNIQUE (A2L_FILE_ID,PARAM_NAME,DEPENDS_ON_PARAM_NAME),
    );


spool off