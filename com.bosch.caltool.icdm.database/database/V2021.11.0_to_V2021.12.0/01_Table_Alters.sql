spool c:\temp\01_Table_Alters.log

---------------------------------------------------------------------
--  ALM Task : 624734 - impl : A CoC (Owner of a questionnaire) can mark a questionnaire definition as "Answers allowed to finish WP"
--
--  To add a new column in T_QUESTION_RESULT_OPTIONS 
---------------------------------------------------------------------

ALTER TABLE T_QUESTION_RESULT_OPTIONS
    ADD (Q_RESULT_ALW_FINISH_WP VARCHAR2(1) DEFAULT 'Y');
    
ALTER TABLE T_QUESTION_RESULT_OPTIONS  
    MODIFY (Q_RESULT_ALW_FINISH_WP NOT NULL);
    

spool off