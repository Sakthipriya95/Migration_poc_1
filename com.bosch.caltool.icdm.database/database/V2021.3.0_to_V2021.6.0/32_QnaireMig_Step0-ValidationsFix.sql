spool D:\RBEI\iCDM\Bebith\V2021.6.0\qnaire_mig\mig_log_migsch2\32_QnaireMig_Step0-Validation_Fix.log

------------------------------------------------------------------------------------------------
-- This script fixes the invalid dependent question id dependencies
-- Note : Should be Ran if there are any invalid dependenciesfound in 32_QnaireMig_Step0-Validations scripts
-- 'commit' the records manually after script finishes.
-- To verify the changes, run '32_QnaireMig_Step0-Validation.sql' again
------------------------------------------------------------------------------------------------

set serveroutput on

declare
    v_count number;
    v_dep_q_name_eng t_question.q_name_eng%TYPE;
    v_valid_dep_ques_id number;
    
begin

      DBMS_OUTPUT.PUT_LINE(to_char( SYSDATE, 'YYYY-MM-DD HH24:MI:SS' ) || ' - Start validation');

      For f_t_qnaire_version in (select * from t_questionnaire_version) loop
        FOR f_t_question in (select * from t_question where qnaire_vers_id = f_t_qnaire_version.qnaire_vers_id )LOOP
        if f_t_question.DEP_QUES_ID is not null then 
            select count(1) into v_count from t_question where Q_ID = f_t_question.DEP_QUES_ID and qnaire_vers_id = f_t_qnaire_version.qnaire_vers_id;
            if v_count = 0 then
                select q_name_eng into v_dep_q_name_eng from t_question where q_id = f_t_question.DEP_QUES_ID;   
                select q_id into v_valid_dep_ques_id from t_question where q_name_eng = v_dep_q_name_eng and qnaire_vers_id = f_t_qnaire_version.qnaire_vers_id;
                
                
                DBMS_OUTPUT.PUT_LINE('Qnaire Version - '|| f_t_qnaire_version.qnaire_vers_id 
                        || ', QID - ' || f_t_question.Q_ID || '(' || f_t_question.Q_NUMBER 
                        || ') : Valid Dependent Question ID : '|| v_valid_dep_ques_id ||' :Invalid Dependent Question ID found : '|| f_t_question.DEP_QUES_ID);
                
                
                update t_question set DEP_QUES_ID = v_valid_dep_ques_id where q_id = f_t_question.Q_ID;
            end if;
        end if ;
        END LOOP;
        
    end loop;

    DBMS_OUTPUT.PUT_LINE(to_char( SYSDATE, 'YYYY-MM-DD HH24:MI:SS' ) || ' - End validation');

end; 
/

spool off
