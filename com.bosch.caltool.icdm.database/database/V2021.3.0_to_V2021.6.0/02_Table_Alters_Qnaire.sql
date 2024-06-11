spool c:\temp\02_Table_Alters_Qnaire.log

------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 567145: Create tables and Constrains based on ER diagram for questionnaire enhancements
------------------------------------------------------------------------------------------------------------------

----------------------------------
--------t_rvw_qnaire_resp_versions
----------------------------------
-- create t_rvw_qnaire_resp_versions
CREATE TABLE t_rvw_qnaire_resp_versions (
    qnaire_resp_vers_id   NUMBER NOT NULL,
    qnaire_resp_id        NUMBER NOT NULL,
    name                  VARCHAR2(100) NOT NULL,
    description           VARCHAR2(4000),
    rev_num               NUMBER NOT NULL,
    created_user          VARCHAR2(30) NOT NULL,
    created_date          TIMESTAMP NOT NULL,
    modified_date         TIMESTAMP,
    modified_user        VARCHAR2(30),
    version               NUMBER NOT NULL
);

--create PRIMARY KEY
ALTER TABLE t_rvw_qnaire_resp_versions 
    ADD CONSTRAINT t_rvw_qnaire_resp_vers_pk PRIMARY KEY ( qnaire_resp_vers_id );


--create FOREIGN KEY                                                                                            
ALTER TABLE t_rvw_qnaire_resp_versions
    ADD CONSTRAINT t_rvw_qnaire_resp_vers_fk1 FOREIGN KEY ( qnaire_resp_id )
        REFERENCES t_rvw_qnaire_response ( qnaire_resp_id );      
        
-- create COLUMN
ALTER TABLE t_rvw_qnaire_resp_versions
  ADD QNAIRE_VERS_ID NUMBER ;
  
ALTER TABLE t_rvw_qnaire_resp_versions
    MODIFY (QNAIRE_VERS_ID NOT NULL);
  
 --create FOREIGN KEY                                                                                            
ALTER TABLE t_rvw_qnaire_resp_versions
    ADD CONSTRAINT t_rvw_qnaire_resp_vers_fk2 FOREIGN KEY ( QNAIRE_VERS_ID )
        REFERENCES t_questionnaire_version ( QNAIRE_VERS_ID );  
        
--585091 - Determine the state of questionnaire response
-- create a column for qnaire_vers_status 
ALTER TABLE t_rvw_qnaire_resp_versions 
    ADD QNAIRE_VERS_STATUS VARCHAR2(1);

ALTER TABLE t_rvw_qnaire_resp_versions
    MODIFY (QNAIRE_VERS_STATUS NOT NULL);

----------------------------------
--------t_rvw_qnaire_resp_variants
----------------------------------        
      
-- create t_rvw_qnaire_resp_variants --
CREATE TABLE t_rvw_qnaire_resp_variants (
    qnaire_resp_var_id   NUMBER NOT NULL,
    pidc_vers_id         NUMBER(15) NOT NULL,
    variant_id           NUMBER(15) ,
    qnaire_resp_id       NUMBER NOT NULL,
    created_date         TIMESTAMP NOT NULL,
    created_user         VARCHAR2(30) NOT NULL,
    modified_user        VARCHAR2(30),
    modified_date        TIMESTAMP,
    version              NUMBER NOT NULL
);

--create PRIMARY KEY
ALTER TABLE t_rvw_qnaire_resp_variants 
    ADD CONSTRAINT t_rvw_qnaire_resp_var_pk PRIMARY KEY ( qnaire_resp_var_id );



--create FOREIGN KEY
ALTER TABLE t_rvw_qnaire_resp_variants
    ADD CONSTRAINT t_rvw_qnaire_resp_var_fk1 FOREIGN KEY ( pidc_vers_id )
        REFERENCES t_pidc_version ( pidc_vers_id );

ALTER TABLE t_rvw_qnaire_resp_variants
    ADD CONSTRAINT t_rvw_qnaire_resp_var_fk2 FOREIGN KEY ( variant_id )
        REFERENCES tabv_project_variants ( variant_id );

ALTER TABLE t_rvw_qnaire_resp_variants
    ADD CONSTRAINT t_rvw_qnaire_resp_var_fk3 FOREIGN KEY ( qnaire_resp_id )
        REFERENCES t_rvw_qnaire_response ( qnaire_resp_id );    
        
        
----------------------------
--------t_rvw_qnaire_answer
----------------------------
-- create COLUMN
ALTER TABLE t_rvw_qnaire_answer
  ADD QNAIRE_RESP_VERS_ID NUMBER ;
  
-- create CONSTRAINT
ALTER TABLE t_rvw_qnaire_answer
    ADD CONSTRAINT  t_rvw_qnr_ans_fk_resp_vers_id FOREIGN KEY ( qnaire_resp_vers_id )
        REFERENCES t_rvw_qnaire_resp_versions ( qnaire_resp_vers_id );
  
-------------------------------
--------t_rvw_qnaire_response
-------------------------------

-- add COLUMN
ALTER TABLE t_rvw_qnaire_response
  ADD A2L_WP_ID NUMBER ;
  
ALTER TABLE t_rvw_qnaire_response
  ADD A2L_RESP_ID NUMBER ;
  
ALTER TABLE t_rvw_qnaire_response
  ADD DELETED_FLAG VARCHAR2(1 BYTE) DEFAULT 'N' NOT NULL;
  
-- create CONSTRAINT

ALTER TABLE t_rvw_qnaire_response
    ADD CONSTRAINT T_RVW_QNR_RESP_FK_A2L_WP_ID FOREIGN KEY ( A2L_WP_ID )
        REFERENCES T_A2L_WORK_PACKAGES ( A2L_WP_ID );
        
ALTER TABLE t_rvw_qnaire_response
    ADD CONSTRAINT T_RVW_QNR_RESP_FK_A2L_RESP_ID FOREIGN KEY ( A2L_RESP_ID )
        REFERENCES T_A2L_RESPONSIBILITY ( A2L_RESP_ID );
  
  
        
-- Drop Unique CONSTRAINT
--remove T_RVW_QNAIRE_RESPONSE_UK1
ALTER TABLE t_rvw_qnaire_response 
    DROP CONSTRAINT T_RVW_QNAIRE_RESPONSE_UK1;
ALTER TABLE t_rvw_qnaire_response
    DROP INDEX T_RVW_QNAIRE_RESPONSE_UK1;

------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 594290: Store Reviewed User and Reviewed Date in dedicated fields in DB
------------------------------------------------------------------------------------------------------------------
alter table T_RVW_QNAIRE_RESP_VERSIONS add REVIEWED_DATE timestamp(6);

alter table T_RVW_QNAIRE_RESP_VERSIONS add REVIEWED_USER varchar2(30);


----------------------------------------------------------------------------------------------
-------ALM task id:573092 Drop constraint as the unique column QNAIRE_RESP_ID will be removed
----------------------------------------------------------------------------------------------
ALTER TABLE T_RVW_QNAIRE_ANSWER DROP CONSTRAINT T_RVW_QNAIRE_ANSWER_UK1;
DROP INDEX T_RVW_QNAIRE_ANSWER_UK1;

----------------------------------------------------------------------------------------------
---ALM task id:597194  Temp column for migration state 
-- NOTE : to be removed after successful migration
----------------------------------------------------------------------------------------------
ALTER TABLE T_QUESTIONNAIRE_VERSION
    ADD RELATED_QNAIRE_VERS_ID number; 

ALTER TABLE T_QUESTION
    ADD RELATED_Q_ID number; 

ALTER TABLE T_QUESTION_RESULT_OPTIONS
    ADD RELATED_Q_RESULT_OPT_ID number; 

----------------------------------------------------------------------------------------------
---ALM task id:597194  Temp columns for keeping intermediate migration states 
-- NOTE : to be removed after successful migration
----------------------------------------------------------------------------------------------
ALTER TABLE t_rvw_qnaire_resp_versions
    ADD MIG_STATE VARCHAR2(1) DEFAULT 'N' NOT NULL;

ALTER TABLE t_rvw_qnaire_response
    ADD MIG_STATE VARCHAR2(1) DEFAULT 'N' NOT NULL;
    
ALTER TABLE t_rvw_qnaire_results
    ADD MIG_STATE VARCHAR2(2) DEFAULT 'N' NOT NULL;

-- keeps the source RESP from which the record was replicated.
ALTER TABLE T_RVW_QNAIRE_RESPONSE
    ADD COPIED_FROM_QNAIRE_RESP_ID number; 
  

spool off
