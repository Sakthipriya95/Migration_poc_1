spool c:\temp\table_updates.log

--------------------------------------------------------
--  January-17-2014
--------------------------------------------------------
--
-- ICDM-518
--
-- Make NODE_ID column as nullable
--
--
ALTER TABLE TABV_ICDM_FILES MODIFY ( NODE_ID NUMBER);
--
-- ICDM-518
--
-- Tables to store review results
--
--

--------------------------------------------------------
--  DDL for Table T_RVW_RESULTS
--------------------------------------------------------

  CREATE TABLE T_RVW_RESULTS 
   (	RESULT_ID NUMBER PRIMARY KEY, 
	PROJECT_ID NUMBER NOT NULL, 
	VARIANT_ID NUMBER, 
	A2L_ID NUMBER NOT NULL, 
	GRP_WORK_PKG VARCHAR2(100), 
	FC2WP_ID NUMBER, 
	RVW_STATUS VARCHAR2(1) NOT NULL, 
	ORG_RESULT_ID NUMBER, 
	CREATED_DATE DATE NOT NULL, 
	CREATED_USER VARCHAR2(20) NOT NULL, 
	MODIFIED_DATE DATE, 
	MODIFIED_USER VARCHAR2(20), 
	VERSION NUMBER NOT NULL
   ) ;
--------------------------------------------------------
--  Ref Constraints for Table T_RVW_RESULTS
--------------------------------------------------------

  ALTER TABLE T_RVW_RESULTS ADD CONSTRAINT T_RVW_RESULTS_FK1 FOREIGN KEY (PROJECT_ID)
	  REFERENCES TABV_PROJECTIDCARD (PROJECT_ID);
  ALTER TABLE T_RVW_RESULTS ADD CONSTRAINT T_RVW_RESULTS_FK2 FOREIGN KEY (VARIANT_ID)
	  REFERENCES TABV_PROJECT_VARIANTS (VARIANT_ID);
  ALTER TABLE T_RVW_RESULTS ADD CONSTRAINT T_RVW_RESULTS_FK3 FOREIGN KEY (ORG_RESULT_ID)
	  REFERENCES T_RVW_RESULTS (RESULT_ID);

--------------------------------------------------------
--  DDL for Table T_RVW_PARTICIPANTS
--------------------------------------------------------

  CREATE TABLE T_RVW_PARTICIPANTS 
   (	PARTICIPANT_ID NUMBER PRIMARY KEY, 
	RESULT_ID NUMBER NOT NULL, 
	USER_ID NUMBER NOT NULL, 
	ACTIVITY_TYPE VARCHAR2(1) NOT NULL, 
	CREATED_USER VARCHAR2(20) NOT NULL, 
	CREATED_DATE DATE NOT NULL, 
	MODIFIED_USER VARCHAR2(20), 
	MODIFIED_DATE DATE, 
	VERSION NUMBER NOT NULL
   ) ;
--------------------------------------------------------
--  Ref Constraints for Table T_RVW_PARTICIPANTS
--------------------------------------------------------

  ALTER TABLE T_RVW_PARTICIPANTS ADD CONSTRAINT T_RVW_PARTICIPANTS_FK1 FOREIGN KEY (USER_ID)
	  REFERENCES TABV_APIC_USERS (USER_ID);
  ALTER TABLE T_RVW_PARTICIPANTS ADD CONSTRAINT T_RVW_PARTICIPANTS_FK2 FOREIGN KEY (RESULT_ID)
	  REFERENCES T_RVW_RESULTS (RESULT_ID);

   
--------------------------------------------------------
--  DDL for Table T_RVW_FUNCTIONS
--------------------------------------------------------

  CREATE TABLE T_RVW_FUNCTIONS 
   (	RVW_FUN_ID NUMBER PRIMARY KEY, 
	RESULT_ID NUMBER NOT NULL, 
	FUNCTION_ID NUMBER NOT NULL, 
	CREATED_USER VARCHAR2(20) NOT NULL, 
	CREATED_DATE DATE NOT NULL, 
	MODIFIED_USER VARCHAR2(20), 
	MODIFIED_DATE DATE, 
	VERSION NUMBER NOT NULL
   ) ;

--------------------------------------------------------
--  Ref Constraints for Table T_RVW_FUNCTIONS
--------------------------------------------------------

  ALTER TABLE T_RVW_FUNCTIONS ADD CONSTRAINT T_RVW_FUNCTIONS_FK1 FOREIGN KEY (FUNCTION_ID)
	  REFERENCES T_FUNCTIONS (ID);
  ALTER TABLE T_RVW_FUNCTIONS ADD CONSTRAINT T_RVW_FUNCTIONS_FK2 FOREIGN KEY (RESULT_ID)
	  REFERENCES T_RVW_RESULTS (RESULT_ID);
   
--------------------------------------------------------
--  DDL for Table T_RVW_PARAMETERS
--------------------------------------------------------

  CREATE TABLE T_RVW_PARAMETERS 
   (	RVW_PARAM_ID NUMBER PRIMARY KEY, 
	PARAM_ID NUMBER NOT NULL, 
	RESULT_ID NUMBER NOT NULL,
	RVW_FUN_ID NUMBER NOT NULL,
	RVW_METHOD VARCHAR2(1), 
	LOWER_LIMIT NUMBER, 
	UPPER_LIMIT NUMBER, 
	REF_VALUE BLOB,
	UNIT VARCHAR2(255),
	HINT VARCHAR2(4000),
	CHECKED_VALUE BLOB, 
	RESULT VARCHAR2(1), 
	REVIEWED_FLAG VARCHAR2(1) NOT NULL, 
	RVW_COMMENT VARCHAR2(4000), 
	CREATED_USER VARCHAR2(20) NOT NULL, 
	CREATED_DATE DATE NOT NULL, 
	MODIFIED_USER VARCHAR2(20), 
	MODIFIED_DATE DATE, 
	VERSION NUMBER NOT NULL
   ) ;
--------------------------------------------------------
--  Ref Constraints for Table T_RVW_PARAMETERS
--------------------------------------------------------

  ALTER TABLE T_RVW_PARAMETERS ADD CONSTRAINT T_RVW_PARAMETERS_FK1 FOREIGN KEY (PARAM_ID)
	  REFERENCES T_PARAMETER (ID);
  ALTER TABLE T_RVW_PARAMETERS ADD CONSTRAINT T_RVW_PARAMETERS_FK2 FOREIGN KEY (RESULT_ID)
	  REFERENCES T_RVW_RESULTS (RESULT_ID);
  ALTER TABLE T_RVW_PARAMETERS ADD CONSTRAINT T_RVW_PARAMETERS_FK3 FOREIGN KEY (RVW_FUN_ID)
	  REFERENCES T_RVW_FUNCTIONS (RVW_FUN_ID);


--------------------------------------------------------
--  DDL for Table T_RVW_FILES
--------------------------------------------------------

  CREATE TABLE T_RVW_FILES 
   (	RVW_FILE_ID NUMBER PRIMARY KEY, 
	RESULT_ID NUMBER, 
	FILE_ID NUMBER NOT NULL, 
	RVW_PARAM_ID NUMBER, 
	FILE_TYPE VARCHAR2(5) NOT NULL, 
	CREATED_USER VARCHAR2(20) NOT NULL, 
	CREATED_DATE DATE NOT NULL, 
	MODIFIED_USER VARCHAR2(20), 
	MODIFIED_DATE DATE, 
	VERSION NUMBER NOT NULL
   ) ;
--------------------------------------------------------
--  Constraints for Table T_RVW_FILES
--------------------------------------------------------
  ALTER TABLE T_RVW_FILES ADD CONSTRAINT T_RVW_FILES_UK1 UNIQUE (RESULT_ID, RVW_PARAM_ID, FILE_ID);

--------------------------------------------------------
--  Ref Constraints for Table T_RVW_FILES
--------------------------------------------------------

  ALTER TABLE T_RVW_FILES ADD CONSTRAINT T_RVW_FILES_FK1 FOREIGN KEY (RVW_PARAM_ID)
	  REFERENCES T_RVW_PARAMETERS (RVW_PARAM_ID);
  ALTER TABLE T_RVW_FILES ADD CONSTRAINT T_RVW_FILES_FK2 FOREIGN KEY (RESULT_ID)
	  REFERENCES T_RVW_RESULTS (RESULT_ID);
  ALTER TABLE T_RVW_FILES ADD CONSTRAINT T_RVW_FILES_FK3 FOREIGN KEY (FILE_ID)
	  REFERENCES TABV_ICDM_FILES (FILE_ID);
	  
--------------------------------------------------------
--  Inserts SSD_NODE_ID into TABV_COMMON_PARAMS table. Provide the valid value for <Node Id> in the DB
--------------------------------------------------------	  

Insert into TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE,VERSION) values ('SSD_NODE_ID','SSD NodeId to store review rules','<Node Id>',1);
COMMIT;

--------------------------------------------------------
--  Remove T_REVIEW_RULES table
--------------------------------------------------------      

DROP TABLE T_REVIEW_RULES CASCADE CONSTRAINTS;

--------------------------------------------------------
--  Version update script
--------------------------------------------------------      
delete from TABV_COMMON_PARAMS WHERE PARAM_ID = 'iCDM_CLIENT_VERSION';
Insert into TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE,VERSION) values ('iCDM_CLIENT_VERSION','iCDM Client''s current version','1.6.0',1);
COMMIT;


spool off
