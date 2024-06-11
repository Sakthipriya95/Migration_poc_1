spool c:\temp\32_QnaireMig_Step0-Validations.log

------------------------------------------------------------------------------------------------
-- Before beginning migration, run these checks
--  Check : if there are dependent Q IDs assoicated with questions of another def version 
--  Expected : no such cross dependencies present
------------------------------------------------------------------------------------------------

set serveroutput on

declare
    v_count number;
begin

	DBMS_OUTPUT.PUT_LINE(to_char( SYSDATE, 'YYYY-MM-DD HH24:MI:SS' ) || ' - Start validation');

	For f_t_qnaire_version in (select * from t_questionnaire_version) loop
        FOR f_t_question in (select * from t_question where qnaire_vers_id = f_t_qnaire_version.qnaire_vers_id )LOOP
        if f_t_question.DEP_QUES_ID is not null then 
            select count(1) into v_count from t_question where Q_ID = f_t_question.DEP_QUES_ID and qnaire_vers_id = f_t_qnaire_version.qnaire_vers_id;
            if v_count = 0 then
                DBMS_OUTPUT.PUT_LINE('Qnaire Version - '|| f_t_qnaire_version.qnaire_vers_id 
                        || ', QID - ' || f_t_question.Q_ID || '(' || f_t_question.Q_NUMBER 
                        || ') : Invalid Dependent Question ID found : '|| f_t_question.DEP_QUES_ID);
            end if;
        end if ;
        END LOOP;
    end loop;

    DBMS_OUTPUT.PUT_LINE(to_char( SYSDATE, 'YYYY-MM-DD HH24:MI:SS' ) || ' - End validation');

end; 
/
 
spool off
