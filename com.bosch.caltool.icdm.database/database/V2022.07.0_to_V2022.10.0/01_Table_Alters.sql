spool c:\temp\01_Table_Alters.log

---------------------------------------------------------------------  
--------------------------------------------------------------------------------------------------------------------------------
--  ALM Task : 667153 -Additional CheckBox for "No negative answers allowed to finish WP" in questionnaire definition dialog and its DB changes
--  add new column to NO_NEGATIVE_ANSWERS_ALLOWED tostore the value of  'No negative answers allowed to finish WP' check box while
-- creating a questionnaire defenition
--------------------------------------------------------------------------------------------------------------------------------
ALTER TABLE T_QUESTIONNAIRE_VERSION 
    ADD NO_NEGATIVE_ANSWERS_ALLOWED VARCHAR2(1);

---------------------------------------------------------------------------    
-- ALM Task:683225  - Add new compliance related columns for a work package division
--
-- To add a new columns CRP_OBD_RELEVANT_FLAG,CRP_OBD_COMMENT, CRP_EMISSION_RELEVANT_FLAG, CRP_EMISSION_COMMENT, 
-- CRP_SOUND_RELEVANT_FLAG, CRP_SOUND_COMMENT  in table T_WORKPACKAGE_DIVISION.   
----------------------------------------------------------------------------
ALTER TABLE T_WORKPACKAGE_DIVISION 
    ADD (
        CRP_OBD_RELEVANT_FLAG VARCHAR2(1) DEFAULT 'N' NOT NULL,
        CRP_OBD_COMMENT VARCHAR2(4000),
        CRP_EMISSION_RELEVANT_FLAG VARCHAR2(1) DEFAULT 'N' NOT NULL,
        CRP_EMISSION_COMMENT VARCHAR2(4000),
        CRP_SOUND_RELEVANT_FLAG VARCHAR2(1) DEFAULT 'N' NOT NULL,
        CRP_SOUND_COMMENT VARCHAR2(4000)
    ); 
 
---------------------------------------------------------------------------    
-- ALM Task:687334  - Add new column for ARC Released flag for review parameters
--
-- To add a new column ARC_RELEASED_FLAG in table T_RVW_PARAMETERS.   
----------------------------------------------------------------------------
ALTER TABLE T_RVW_PARAMETERS 
    ADD ARC_RELEASED_FLAG VARCHAR2(1 BYTE);

---------------------------------------------------------------------------------------------------    
-- ALM Task:683121  - Impl: EMR Sheets: When uploading out of an Excel EMR Sheet, beside the reference procedure
-- the variant name should be differenciated
--
-- When an excel sheet is uploaded in iCDM and varaint assignment is done,
-- the EMR sheet should be differentiated based on their variant information.
-- Added a new column in T_EMR_PIDC_VARIANT to differentiate the EMR sheets
----------------------------------------------------------------------------------------------------  
ALTER TABLE T_EMR_PIDC_VARIANT 
    ADD EMR_VARIANT NUMBER;

ALTER TABLE T_EMR_PIDC_VARIANT 
    DROP CONSTRAINT T_EMR_PIDC_VAR_EMS_SHT_UK;

--Drop the index created for the above Unique Constraint
drop index T_EMR_PIDC_VAR_EMS_SHT_UK;

ALTER TABLE T_EMR_PIDC_VARIANT
    ADD CONSTRAINT T_EMR_PIDC_VAR_EMS_SHT_UK UNIQUE 
    (
          VARIANT_ID 
        , EMS_ID 
        , EMR_FILE_ID 
        , EMR_VARIANT 
    );
-------------------------------------------------------------------------------------------------------------------
--ALM Task :689706 - Impl : Server side Changes –  Design new table for storing A2l_WP_RESP_Finished status
--------------------------------------------------------------------------------------------------------------------

create table T_A2L_WP_RESPONSIBILITY_STATUS
    (
        A2L_WP_RESP_STATUS_ID NUMBER(15) NOT NULL,
        VARIANT_ID NUMBER(15), 
        WP_RESP_ID NUMBER(15) NOT NULL,
        A2L_RESP_ID NUMBER(15),
        WP_RESP_FIN_STATUS VARCHAR2(1 BYTE) NOT NULL,
        CREATED_DATE   TIMESTAMP (6) NOT NULL, 
        CREATED_USER   VARCHAR2(30 BYTE) NOT NULL, 
        MODIFIED_DATE  TIMESTAMP (6), 
        MODIFIED_USER  VARCHAR2(30 BYTE), 
        VERSION NUMBER(10) NOT NULL,
        
        CONSTRAINT A2L_WP_RESP_STATUS_PK PRIMARY KEY (A2L_WP_RESP_STATUS_ID),
        CONSTRAINT A2L_WP_RESP_STATUS_UK1 UNIQUE (VARIANT_ID,WP_RESP_ID,A2L_RESP_ID),
        CONSTRAINT A2L_WP_RESP_STATUS_FK1 FOREIGN KEY (WP_RESP_ID)
            REFERENCES T_A2L_WP_RESPONSIBILITY (WP_RESP_ID),
        CONSTRAINT A2L_WP_RESP_STATUS_FK2 FOREIGN KEY (VARIANT_ID)
            REFERENCES TABV_PROJECT_VARIANTS (VARIANT_ID),
        CONSTRAINT A2L_WP_RESP_STATUS_FK3 FOREIGN KEY (A2L_RESP_ID)
            REFERENCES T_A2L_RESPONSIBILITY (A2L_RESP_ID)
    );

CREATE INDEX A2L_WP_RESP_STATUS_FK1_IDX ON T_A2L_WP_RESPONSIBILITY_STATUS(VARIANT_ID);
CREATE INDEX A2L_WP_RESP_STATUS_FK2_IDX ON T_A2L_WP_RESPONSIBILITY_STATUS(WP_RESP_ID);
CREATE INDEX A2L_WP_RESP_STATUS_FK3_IDX ON T_A2L_WP_RESPONSIBILITY_STATUS(A2L_RESP_ID);


spool off
