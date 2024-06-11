spool c:\temp\01_Table_Alters.log

--------------------------------------------------------------------------------------------------------------------------------
--  ALM Task : 637826 - Server Side changes , Table Modification for linking WP-RESP
--  Moving 2 columns (a2l_resp_id, a2l_wp_id) from t_rvw_qnaire_response to t_rvw_qnaire_resp_variants table
--  Removing unused columns from t_rvw_qnaire_response table
--  Data Migration of 2 columns from t_rvw_qnaire_response to t_rvw_qnaire_resp_variants table
--------------------------------------------------------------------------------------------------------------------------------

-- Adding New Columns (NOT NULL not given as data is present in table)
ALTER TABLE t_rvw_qnaire_resp_variants 
    ADD a2l_resp_id NUMBER;

ALTER TABLE t_rvw_qnaire_resp_variants 
    ADD a2l_wp_id NUMBER;
    
-- Adding Foreign Keys 
ALTER TABLE t_rvw_qnaire_resp_variants
    ADD CONSTRAINT t_rvw_qnaire_resp_var_fk4 FOREIGN KEY ( A2L_RESP_ID )
        REFERENCES T_A2L_RESPONSIBILITY ( A2L_RESP_ID );

ALTER TABLE t_rvw_qnaire_resp_variants
    ADD CONSTRAINT t_rvw_qnaire_resp_var_fk5 FOREIGN KEY ( A2L_WP_ID )
        REFERENCES T_A2L_WORK_PACKAGES ( A2L_WP_ID );
        
--Modifying Unique Constraint       
ALTER TABLE t_rvw_qnaire_resp_variants 
    DROP CONSTRAINT T_RVW_QNAIRE_RESP_VAR_UK_1;    

ALTER TABLE t_rvw_qnaire_resp_variants
    ADD CONSTRAINT T_RVW_QNAIRE_RESP_VAR_UK_1 UNIQUE 
    (   PIDC_VERS_ID,
        VARIANT_ID,
        A2L_RESP_ID,
        A2L_WP_ID,
        QNAIRE_RESP_ID
    );
    
-- Migration Query:
MERGE INTO t_rvw_qnaire_resp_variants resp_var
    USING t_rvw_qnaire_response resp 
    ON ( resp_var.qnaire_resp_id = resp.qnaire_resp_id )
    WHEN MATCHED THEN UPDATE
    SET resp_var.a2l_resp_id = resp.a2l_resp_id,
        resp_var.a2l_wp_id = resp.a2l_wp_id;

commit;

-- Creating index for foreign keys
CREATE INDEX t_rvw_qnaire_resp_var_indx_1 ON
        t_rvw_qnaire_resp_variants (
            A2L_WP_ID
        );
        
CREATE INDEX t_rvw_qnaire_resp_var_indx_2 ON
        t_rvw_qnaire_resp_variants (
            A2L_RESP_ID
        );
        

-- Adding Not Null constraint after data migration
ALTER TABLE t_rvw_qnaire_resp_variants MODIFY
    a2l_resp_id not null;
    
ALTER TABLE t_rvw_qnaire_resp_variants MODIFY
    a2l_wp_id not null;


--------------------------------------------------------------------------------------------------------------------------------
--  ALM Task : 697536 - Data Review Report/HEX Compare: Add flag on PIDC level to disables using reviews of old PIDC versions in reports
--------------------------------------------------------------------------------------------------------------------------------

ALTER TABLE TABV_PROJECTIDCARD
    ADD (INCL_RVW_OF_OLD_VERS VARCHAR2(1 CHAR) DEFAULT 'Y' NOT NULL);


--------------------------------------------------------------------------------------------------------------------------------
-- ALM Task : 637861 Impl: Par2WP: create work packages from functions when only the default variant group exists (Part 2)
--------------------------------------------------------------------------------------------------------------------------------
CREATE GLOBAL TEMPORARY TABLE GTT_COUNT_RESP_FREQ
(   
    WP_RESP_ID NUMBER(15), 
    PAR_A2L_RESP_ID NUMBER(15), 
    RESP_COUNT NUMBER
) ON COMMIT DELETE ROWS;

----------------------------------------------------------------------------------------
--  ALM Task : 708996 - create table T_Rule_Links to store Links for rule in Rulesets
----------------------------------------------------------------------------------------
CREATE TABLE T_RULE_LINKS (
    RULE_LINK_ID  NUMBER NOT NULL,
    RULE_ID         NUMBER NOT NULL, 
    REV_ID          NUMBER NOT NULL, 
    LINK            VARCHAR2(1000 CHAR) NOT NULL, 
    DESC_ENG        VARCHAR2(2000 CHAR) NOT NULL, 
    DESC_GER        VARCHAR2(2000 CHAR), 
    CREATED_USER    VARCHAR2(30 CHAR) NOT NULL , 
    CREATED_DATE    TIMESTAMP (6) NOT NULL, 
    MODIFIED_USER   VARCHAR2(30 CHAR), 
    MODIFIED_DATE   TIMESTAMP (6), 
    VERSION         NUMBER NOT NULL,
    
    PRIMARY KEY (RULE_LINK_ID),
    CONSTRAINT T_RULE_LINKS_UNI UNIQUE (RULE_ID, REV_ID, DESC_ENG)
   );

-- Temporary table to fetch rule Links for Functions/Rulesets
CREATE GLOBAL TEMPORARY TABLE GTT_RULE_LINKS
   ( ID          NUMBER NOT NULL,        
     RULE_ID     NUMBER NOT NULL, 
     REV_ID      NUMBER NOT NULL,
     PRIMARY KEY (ID)
   ) 
   ON COMMIT DELETE ROWS ;

------------------------------------------------------------------------------------------------------------------------------
------708988: Add New Column in RuleSet Editor
-----Create tables
------------------------------------------------------------------------------------------------------------------------------

CREATE TABLE T_RULESET_PARAM_TYPE(
    PARAM_TYPE_ID  NUMBER NOT NULL,
    PARAM_TYPE     VARCHAR2(500 CHAR) NOT NULL,
    CREATED_DATE   TIMESTAMP (6) NOT NULL, 
    CREATED_USER   VARCHAR2(30 CHAR) NOT NULL, 
    MODIFIED_DATE  TIMESTAMP (6), 
    MODIFIED_USER  VARCHAR2(30 CHAR), 
    VERSION        NUMBER(10) NOT NULL,
    
    CONSTRAINT RULESET_PARAM_TYPE_PK PRIMARY KEY (PARAM_TYPE_ID)   
);


CREATE TABLE T_RULESET_PARAM_RESP(
    PARAM_RESP_ID  NUMBER NOT NULL,
    PARAM_RESP     VARCHAR2(500 CHAR) NOT NULL,
    CREATED_DATE   TIMESTAMP (6) NOT NULL, 
    CREATED_USER   VARCHAR2(30 CHAR) NOT NULL, 
    MODIFIED_DATE  TIMESTAMP (6), 
    MODIFIED_USER  VARCHAR2(30 CHAR), 
    VERSION        NUMBER(10) NOT NULL,

    CONSTRAINT RULESET_PARAM_RESP_PK PRIMARY KEY (PARAM_RESP_ID) 
);

CREATE TABLE T_RULESET_SYS_ELEMENT (
    SYS_ELEMENT_ID  NUMBER NOT NULL,
    SYS_ELEMENT     VARCHAR2(500 CHAR) NOT NULL,
    CREATED_DATE    TIMESTAMP (6) NOT NULL, 
    CREATED_USER    VARCHAR2(30 CHAR) NOT NULL, 
    MODIFIED_DATE   TIMESTAMP (6), 
    MODIFIED_USER   VARCHAR2(30 CHAR), 
    VERSION         NUMBER(10) NOT NULL,

    CONSTRAINT RULESET_SYS_ELEMENT_PK PRIMARY KEY (SYS_ELEMENT_ID)
);

CREATE TABLE T_RULESET_HW_COMPONENT(
    HW_COMPONENT_ID  NUMBER NOT NULL,
    HW_COMPONENT     VARCHAR2(500 CHAR) NOT NULL,
    CREATED_DATE    TIMESTAMP (6) NOT NULL, 
    CREATED_USER    VARCHAR2(30 CHAR) NOT NULL, 
    MODIFIED_DATE   TIMESTAMP (6), 
    MODIFIED_USER   VARCHAR2(30 CHAR), 
    VERSION         NUMBER(10) NOT NULL,

    CONSTRAINT RULESET_HW_COMPONENT_PK PRIMARY KEY (HW_COMPONENT_ID) 
);

------------------------------------------------------------------------------------------------------------------------------
---708988: Add New Column in RuleSet Editor
----Add columns to T_RULE_SET_PARAMS
------------------------------------------------------------------------------------------------------------------------------

ALTER TABLE T_RULE_SET_PARAMS
  ADD (
    PARAM_TYPE_ID NUMBER,
    PARAM_RESP_ID NUMBER,
    SYS_ELEMENT_ID NUMBER,
    HW_COMPONENT_ID NUMBER
  );

ALTER TABLE T_RULE_SET_PARAMS
    ADD CONSTRAINT RULE_SET_PARAMS_FK4 FOREIGN KEY (PARAM_TYPE_ID)
        REFERENCES T_RULESET_PARAM_TYPE(PARAM_TYPE_ID);

ALTER TABLE T_RULE_SET_PARAMS
    ADD CONSTRAINT RULE_SET_PARAMS_FK5 FOREIGN KEY (PARAM_RESP_ID)
        REFERENCES T_RULESET_PARAM_RESP(PARAM_RESP_ID);

ALTER TABLE T_RULE_SET_PARAMS
    ADD CONSTRAINT RULE_SET_PARAMS_FK6 FOREIGN KEY (SYS_ELEMENT_ID)
        REFERENCES T_RULESET_SYS_ELEMENT(SYS_ELEMENT_ID);

ALTER TABLE T_RULE_SET_PARAMS
    ADD CONSTRAINT RULE_SET_PARAMS_FK7 FOREIGN KEY (HW_COMPONENT_ID)
        REFERENCES T_RULESET_HW_COMPONENT(HW_COMPONENT_ID);
  
------------------------------------------------------------------------------------------------------------------------------
---708988: Add New Column in RuleSet Editor
----Create Index on Foreign Key for the tables 
------------------------------------------------------------------------------------------------------------------------------
CREATE INDEX RULE_SET_PARAMS_FK4_IDX ON T_RULE_SET_PARAMS(PARAM_TYPE_ID);

CREATE INDEX RULE_SET_PARAMS_FK5_IDX ON T_RULE_SET_PARAMS(PARAM_RESP_ID);

CREATE INDEX RULE_SET_PARAMS_FK6_IDX ON T_RULE_SET_PARAMS(SYS_ELEMENT_ID);

CREATE INDEX RULE_SET_PARAMS_FK7_IDX ON T_RULE_SET_PARAMS(HW_COMPONENT_ID);
   
spool off

