spool c:\temp\01_Table_Alters.log

------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 539935: Create store procedure for copying wp assignments from one a2l to other 
--  Create temprory table for store procedure
------------------------------------------------------------------------------------------------------------------
 CREATE GLOBAL TEMPORARY TABLE GTT_PARAM_MAPPING
   (
        PARAM_ID NUMBER(15,0),
        WP_RESP_ID NUMBER(15,0)
   ) ON COMMIT DELETE ROWS ;

------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 552641: Add Flag in Questionnaires Definition Editor 'Result not relevant for overall status of QNaire' 
--  Add result relevant column in t_question table
------------------------------------------------------------------------------------------------------------------
ALTER TABLE T_QUESTION 
    ADD ( RESULT_RELEVANT_FLAG VARCHAR2(1) DEFAULT 'Y' NOT NULL );

------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 555704:  Add a comment field on review result level on tab page 'Review Information'
------------------------------------------------------------------------------------------------------------------
ALTER TABLE T_RVW_RESULTS 
ADD (
        COMMENTS VARCHAR2(4000)
);


spool off
