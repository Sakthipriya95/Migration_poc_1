spool c:\temp\32_Table_Data_Q_result_option_migration.log

------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 553722: DB changes - new table has to be created to store the assessment and 
--  result for every individual questions and also data has to be inserted for existing questions
------------------------------------------------------------------------------------------------------------------

-- Migration script to a) create the results in T_QUESTION_RESULT_OPTIONS for the existing question results
--                    b) map the results in T_QUESTION_RESULT_OPTIONS to the existing questionnaire response answers

DECLARE
    CURSOR cur_question IS SELECT * FROM T_QUESTION;
    CURSOR cur_question_res IS select * from T_RVW_QNAIRE_ANSWER;

BEGIN 
    FOR question_value IN cur_question
    LOOP
        IF question_value.POSITIVE_RESULT = 'Y' THEN
            INSERT INTO T_QUESTION_RESULT_OPTIONS (Q_ID,Q_RESULT_NAME, Q_RESULT_TYPE) VALUES (question_value.Q_ID, 'Yes','P');
            INSERT INTO T_QUESTION_RESULT_OPTIONS (Q_ID,Q_RESULT_NAME, Q_RESULT_TYPE) VALUES (question_value.Q_ID, 'No','N');
        ELSIF question_value.POSITIVE_RESULT = 'N' THEN
            INSERT INTO T_QUESTION_RESULT_OPTIONS (Q_ID,Q_RESULT_NAME, Q_RESULT_TYPE) VALUES (question_value.Q_ID, 'No','P');
            INSERT INTO T_QUESTION_RESULT_OPTIONS (Q_ID,Q_RESULT_NAME, Q_RESULT_TYPE) VALUES (question_value.Q_ID, 'Yes','N');
        ELSIF question_value.POSITIVE_RESULT = 'F' THEN
            INSERT INTO T_QUESTION_RESULT_OPTIONS (Q_ID,Q_RESULT_NAME, Q_RESULT_TYPE) VALUES (question_value.Q_ID, 'Finished','P');
            INSERT INTO T_QUESTION_RESULT_OPTIONS (Q_ID,Q_RESULT_NAME, Q_RESULT_TYPE) VALUES (question_value.Q_ID, 'Not Finished','N');
        END IF;

    END LOOP;
    
    FOR question_Res_Val IN cur_question_res
    LOOP
        IF question_Res_Val.RESULT = 'P' THEN
            UPDATE T_RVW_QNAIRE_ANSWER 
            SET Q_RESULT_OPT_ID = 
                (select Q_RESULT_OPT_ID from T_QUESTION_RESULT_OPTIONS where q_id = question_Res_Val.Q_ID  and Q_RESULT_TYPE = 'P' ) 
            where rvw_answer_id = question_Res_Val.rvw_answer_id;
            
        ELSIF question_Res_Val.RESULT = 'N' THEN
            UPDATE T_RVW_QNAIRE_ANSWER  
            SET Q_RESULT_OPT_ID = 
                (select Q_RESULT_OPT_ID from T_QUESTION_RESULT_OPTIONS where q_id = question_Res_Val.Q_ID  and Q_RESULT_TYPE = 'N' )
            where rvw_answer_id = question_Res_Val.rvw_answer_id;
            
        END IF;

    END LOOP;
    
    commit;
    
END;
/

spool off
