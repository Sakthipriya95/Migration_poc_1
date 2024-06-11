spool c:\temp\01_Table_Alters.log

---------------------------------------------------------------------
--  ALM Task : 618045 - impl : Add context menu option 'Templates for Comments' to Review Results
---------------------------------------------------------------------
CREATE TABLE T_RVW_COMMENT_TEMPLATES 
(
	  COMMENT_ID NUMBER NOT NULL,
	  COMMENT_DESC VARCHAR2(4000) NOT NULL,
	  CREATED_USER VARCHAR2(50) NOT NULL,
	  CREATED_DATE TIMESTAMP NOT NULL,
	  MODIFIED_USER VARCHAR2(50),
	  MODIFIED_DATE TIMESTAMP,
	  VERSION NUMBER NOT NULL,
	  CONSTRAINT T_RVW_CMT_TMPLATE_PK PRIMARY KEY 
	  (
	    COMMENT_ID 
	  ),
	  CONSTRAINT T_RVW_CMT_TMPLATE_UK_DESC UNIQUE 
	  (
	  	COMMENT_DESC 
      )
);


--------------------------------------------------------------------------------------------------------------------------------
-- ALM Task : 618047 - impl : Impl - Add additional context menu option 'Last comments' from which the user can select.
--------------------------------------------------------------------------------------------------------------------------------
  
--------------------------------------------------------------------------------------------------------------------------------
-- ALM Task : 622652 - Impl - Review comment history - Improvements
--------------------------------------------------------------------------------------------------------------------------------
   
--create table T_RVW_USER_CMNT_HISTORY
CREATE TABLE T_RVW_USER_CMNT_HISTORY 
(
    RVW_USER_CMNT_HISTORY_ID NUMBER NOT NULL, 
    RVW_COMMENT VARCHAR2(4000 BYTE) NOT NULL, 
    RVW_CMNT_USER_ID NUMBER NOT NULL, 
    VERSION NUMBER NOT NULL, 
    CREATED_DATE DATE NOT NULL, 
    CREATED_USER VARCHAR2(20 BYTE) NOT NULL, 
    MODIFIED_DATE DATE, 
    MODIFIED_USER VARCHAR2(20 BYTE)
);
    
ALTER TABLE T_RVW_USER_CMNT_HISTORY ADD CONSTRAINT T_RVW_CMNT_HISTORY_PK 
  PRIMARY KEY (RVW_USER_CMNT_HISTORY_ID);

ALTER TABLE T_RVW_USER_CMNT_HISTORY ADD CONSTRAINT T_RVW_USER_CMNT_HISTORY_FK1 
  FOREIGN KEY (RVW_CMNT_USER_ID)
  REFERENCES TABV_APIC_USERS (USER_ID);

ALTER TABLE T_RVW_USER_CMNT_HISTORY ADD CONSTRAINT T_RVW_USER_CMNT_HISTORY_UK1 
  UNIQUE (RVW_COMMENT, RVW_CMNT_USER_ID) ;

---------------------------------------------------------------------  
--------------------------------------------------------------------------------------------------------------------------------
--  ALM Task : 620967 - add new column to T_QUESTIONNAIRE_VERSION to store questionnaire version as
--  'General Questionnaire not required when adding this questionnaire'
--------------------------------------------------------------------------------------------------------------------------------
ALTER TABLE T_QUESTIONNAIRE_VERSION ADD GENERAL_QUES_EQUIVALENT VARCHAR2(1);

spool off
