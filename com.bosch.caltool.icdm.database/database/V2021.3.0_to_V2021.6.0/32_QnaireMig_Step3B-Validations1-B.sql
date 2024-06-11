spool c:\temp\32_QnaireMig_Step3B-Validations1-B.log


-------------------------------------------------
-- Step 3 Validation Scripts
--Note : Do Not Run 32_qnaire_Step3C unless needed
-------------------------------------------------

-------------------------------------------------
--Script to identify the pidc versions that doesn't have the common general questions for a specific a2l wp and resp combination 
--Note : Run 32_QnaireMig_Step3C-DoNotRunUnlessIssuesFoundByValidationScript1.sql only if there are any PIDC version found without general questions
-------------------------------------------------
set serveroutput on size unlimited
declare

    v_genereal_question_found number;
    v_pidc_vers_id number;
    CURSOR pidc_vers_cur IS 
        select distinct pidc_vers_id from t_rvw_qnaire_response;

begin
    dbms_output.put_line('General Question Not found for the pidc versions');
    open pidc_vers_cur;
    LOOP
        BEGIN
            FETCH pidc_vers_cur into v_pidc_vers_id ; 
            EXIT WHEN pidc_vers_cur%notfound;
             
            FOR v_qnaire_resp in (select * from t_rvw_qnaire_response where pidc_vers_id = v_pidc_vers_id) 
            LOOP
                
                if v_qnaire_resp.variant_id is null 
                then 
                    --if variant id is null
                    select count(1) into v_genereal_question_found
                        from t_questionnaire_version 
                        where  qnaire_id = 789119165 
                            and qnaire_vers_id in ( select QNAIRE_VERS_ID 
                                                        from t_rvw_qnaire_response 
                                                        where pidc_vers_id = v_qnaire_resp.pidc_vers_id 
                                                            and variant_id is null
                                                            and a2l_wp_id = v_qnaire_resp.a2l_wp_id 
                                                            and a2l_resp_id = v_qnaire_resp.a2l_resp_id);
                
                else
                    --if variant id is not null
                    select count(1) into v_genereal_question_found
                        from t_questionnaire_version 
                        where  qnaire_id = 789119165 
                            and qnaire_vers_id in ( select QNAIRE_VERS_ID 
                                                        from t_rvw_qnaire_response 
                                                        where pidc_vers_id = v_qnaire_resp.pidc_vers_id 
                                                            and variant_id = v_qnaire_resp.variant_id 
                                                            and a2l_wp_id = v_qnaire_resp.a2l_wp_id 
                                                            and a2l_resp_id = v_qnaire_resp.a2l_resp_id);
                                                            
                end if;
                
                if v_genereal_question_found = 0 
                then
                    --dbms_output.put_line('Pidc Version Id :  A2L WP ID : A2L RESP ID : Variant ID: '|| v_qnaire_resp.pidc_vers_id ||' '|| v_qnaire_resp.a2l_wp_id || ' ' || v_qnaire_resp.a2l_resp_id ||' ' ||v_qnaire_resp.variant_id );
                    --to print the pidc version id
                    dbms_output.put_line( v_qnaire_resp.pidc_vers_id);
                end if ;
                
            END LOOP;
            
        END;
        
    END LOOP;   
    close pidc_vers_cur;
end;
/

spool off
