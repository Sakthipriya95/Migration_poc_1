spool c:\temp\32_QnaireMig_Step2B-Validation2.log


-------------------------------------------------
--Step 2 Validation Scripts - 2
-------------------------------------------------
-- Validation 2 : check whether the result_option id mapped to rvw answers belong to their own questions
--
-- Expected : no 'Invalid Result option found for Rvw Answer' records
-------------------------------------------------------------------------------------------------------

set serveroutput on size unlimited

declare 
    v_res_opt_count number;
begin 

    DBMS_OUTPUT.PUT_LINE(to_char( SYSDATE, 'YYYY-MM-DD HH24:MI:SS' ) || ' - Start validation');

    for f_qnaire_answer in (select * from T_RVW_QNAIRE_ANSWER) 
    loop
        if f_qnaire_answer.Q_RESULT_OPT_ID is not null 
        then
            select count(1) into v_res_opt_count 
            from T_QUESTION_RESULT_OPTIONS 
            where q_id in (f_qnaire_answer.Q_ID) and Q_RESULT_OPT_ID = f_qnaire_answer.Q_RESULT_OPT_ID;
            
            if v_res_opt_count = 0 then
                dbms_output.put_line('Invalid Result option found for Rvw Answer : '|| f_qnaire_answer.RVW_ANSWER_ID);
            end if ;
        end if;
    end loop;

    DBMS_OUTPUT.PUT_LINE(to_char( SYSDATE, 'YYYY-MM-DD HH24:MI:SS' ) || ' - End validation');

end;
/

spool off
