spool c:\temp\32_QnaireMig_Step3B-Validations2.log


-------------------------------------------------
-- Step 3 Validation Scripts
--Note : Do Not Run 32_qnaire_Step3C unless needed
-------------------------------------------------

-------------------------------------------------
--Script to find the questionnaire response that doens't have merged questionnaire response versions with both general and exisiting answers
--Note : Do Not Run 32_QnaireMig_Step3C-DoNotRunUnlessIssuesFoundByValidationScript2.sql unless if there are any PIDC version found without merged questionnaire response versions
-------------------------------------------------
set serveroutput on size unlimited
declare
    v_pidc_vers_id number;
    v_migrated_vers_found number;
    v_gen_ques_found number;

    v_migration_not_happened varchar2(1);
    CURSOR pidc_vers_cur IS 
        select distinct pidc_vers_id from t_rvw_qnaire_response;

begin 
    open pidc_vers_cur;
    LOOP
        BEGIN
            FETCH pidc_vers_cur into v_pidc_vers_id ; 
            EXIT WHEN pidc_vers_cur%notfound;
            --v_migration_not_happened := 'N';
            FOR v_qnaire_resp in (select * from t_rvw_qnaire_response where pidc_vers_id = v_pidc_vers_id) 
            LOOP
                select count(1) into v_gen_ques_found 
                from t_questionnaire_version 
                where  qnaire_id = 789119165 and qnaire_vers_id = v_qnaire_resp.qnaire_vers_id;
                
                --below special versions creation should not be created for general question version
                if v_gen_ques_found = 0 then 
                    select count(1) into v_migrated_vers_found  
                        from t_rvw_qnaire_resp_versions 
                        where qnaire_resp_id =v_qnaire_resp.qnaire_resp_id 
                            and name like '%Migrated version with both general and exisiting question%';
                    
                    if v_migrated_vers_found = 0 then
                       -- v_migration_not_happened := 'Y';
                        --dbms_output.put_line('Pidc Version Id :  A2L WP ID : A2L RESP ID : Variant ID: '|| v_qnaire_resp.pidc_vers_id ||' '|| v_qnaire_resp.a2l_wp_id || ' ' || v_qnaire_resp.a2l_resp_id ||' ' ||v_qnaire_resp.variant_id );
                        dbms_output.put_line('Pidc Version Id '|| v_qnaire_resp.pidc_vers_id);
                        exit;
                    end if;
                end if;
                
            END LOOP;
           --if v_migration_not_happened = 'Y' then
             --   pk_log.info('Pidc Version Id '|| v_pidc_vers_id);
           -- end if;
        END;
        
    END LOOP; 
    close pidc_vers_cur;
end;
/

spool off
