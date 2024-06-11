spool c:\temp\32_QnaireMig_Step4A-MergingAnswersToGeneralQuestions.log


set serveroutput on size unlimited

WHENEVER SQLERROR EXIT SQL.SQLCODE ROLLBACK;

declare

    cBATCH_COMMIT_SIZE constant number := 20;
       
    v_gen_qnaire_vers_id number;
    
    v_general_ques_id number;
        
    --icdm:pidvid,14235457581
    --icdm:pidvid,14241674831
    --icdm:pidvid,14445559537
    --icdm:pidvid,1423099081
    --Input PIDC VERSIon
    --V_temp_test_pidc_vers_id number:=14452963331;

    --******************************************************************************************************
    --Start to find the question name for merging muliptle general question comment to new general question
    --******************************************************************************************************
    PROCEDURE find_qnaire_name
        (p_qnaire_vers_id IN NUMBER, po_qnaire_name OUT varchar2) 
    IS
    
        v_wp_div_id number;
        
    BEGIN 
        select name_eng, wp_div_id into po_qnaire_name, v_wp_div_id 
        from t_questionnaire 
        where qnaire_id in 
          (
            select t_questionnaire_version.QNAIRE_ID 
            from t_questionnaire_version 
            where qnaire_vers_id = p_qnaire_vers_id
          );
        
        IF po_qnaire_name is null 
        THEN
            select wp_name_e into po_qnaire_name 
            from t_workpackage 
            where wp_id in 
              (
                select wp_id 
                from t_workpackage_division 
                where wp_div_id = v_wp_div_id
              );        
        END IF;
        
        pk_log.info ('Qnaire name of p_qnaire_vers_id = ' || p_qnaire_vers_id || ' is : ' || po_qnaire_name);
        
    END;

    --***************************************************************************
    --for merging multiple general question answers to new general question
    --***************************************************************************
    PROCEDURE merge_general_question_answer
        (p_gen_qnaire_id IN NUMBER, p_local_gen_qnaire_vers_id IN NUMBER) 
    IS 	
        
        --to hold question count
        gen_qes_count number;
        --to hold rvw ans count
        rvw_ans_count  number;
        --to load questionnaire name and comments 
        v_qnaire_name varchar2(255);
        
        --icdm:pidvid,781577620
        --icdm:pidvid,10827299181
        --v_input_pidc_vers_id number := V_temp_test_pidc_vers_id;--794154880;
        v_fetched_question t_question%ROWTYPE;
        --for general question
        v_gen_qnaire_resp_id number;
        v_question_gen t_question%ROWTYPE;
        v_rvw_gen_qnaire_answer t_rvw_qnaire_answer%ROWTYPE;
        v_gen_ques_res_opt t_question_result_options%ROWTYPE;
        
        v_rvw_qnaire_resp_ver number;
        
        v_temp_print_result varchar2(255);
        
        v_temp_qnaire_resp_ver_id number;
        
        v_link_already_found number;
        --count of general question should be 1
        v_count_general_question number;
        
        v_remark_length number;
        
        v_commiter_counter number;
        
        v_gen_ques_count number;
        v_parent_ques t_question%ROWTYPE;
        v_match_parent_ques t_question%ROWTYPE;
        
        v_qnaire_comment varchar2(32767):= null;
        v_upd_result t_rvw_qnaire_answer.RESULT%TYPE;
        v_upd_result_opt_id t_rvw_qnaire_answer.Q_RESULT_OPT_ID%TYPE;
    BEGIN
    
        v_commiter_counter := 0;
        
        pk_log.info ('Local General Question Resp Id : ' || p_local_gen_qnaire_vers_id); 
        
        --FOR f_pidc_vers_id in (select DISTINCT PIDC_VERS_ID from t_rvw_qnaire_response order by pidc_vers_id) LOOP
        FOR f_a2l_wp_resp_id IN 
            ( 
              select distinct 
                    resp.pidc_vers_id
                  , nvl(resp.variant_id, -1) AS "VARIANT_ID"
                  , resp.a2l_wp_id
                  , resp.a2l_resp_id
              --distinct a.qnaire_resp_id 
              from t_rvw_qnaire_response resp
                  join t_rvw_qnaire_answer a on (resp.QNAIRE_RESP_ID = a.QNAIRE_RESP_ID)
                  join v_qn_questions q using (q_id)
                  left join t_rvw_qnaire_response resp_gen on (    resp_gen.pidc_vers_id      = resp.pidc_vers_id
                                                               and nvl(resp_gen.variant_id, -1) = nvl(resp.variant_id, -1)
                                                               and resp_gen.a2l_wp_id = resp.a2l_wp_id
                                                               and resp_gen.a2l_resp_id = resp.a2l_resp_id )
              where q.qnaire_id = p_gen_qnaire_id
                   and q.wp_name_e = '<No WP>'
                   and (
                           a.result is not null
                        or a.remark is not null
                        or exists (select 1 from t_links l where l.node_ID  = a.RVW_ANSWER_ID) 
                       )
                   and resp_gen.QNAIRE_VERS_ID in (select QNAIRE_VERS_ID from v_qn_questionnaires where qnaire_id = p_gen_qnaire_id)
                   and not exists (  
                                     select 1 
                                     from t_rvw_qnaire_response gen_resp
                                     where gen_resp.QNAIRE_VERS_ID = resp.QNAIRE_VERS_ID
                                       and gen_resp.QNAIRE_VERS_ID in (select QNAIRE_VERS_ID from v_qn_questionnaires where qnaire_id = p_gen_qnaire_id) 
                                   ) 
                   and resp.MIG_STATE = 'N' and resp.A2L_WP_ID is not null and resp.A2L_RESP_ID is not null                
              group by
                    q_id
                  , resp.pidc_vers_id
                  , nvl(resp.variant_id, -1)
                  , resp.a2l_wp_id
                  , resp.a2l_resp_id
              having count(*) > 1
            ) 
        LOOP
        
            pk_log.info ('Start merge for combination of pidc version, variant, wp, resp => ');
            pk_log.info ('Local General Question Resp Id : ' || p_local_gen_qnaire_vers_id || ', Pidc Vers Id : '|| f_a2l_wp_resp_id.pidc_vers_id);
            pk_log.info ('Variant id : ' || f_a2l_wp_resp_id.VARIANT_ID);
            pk_log.info ('A2L WP and RESP ID : '|| f_a2l_wp_resp_id.A2L_WP_ID || ' & '|| f_a2l_wp_resp_id.A2L_RESP_ID);
            
            --fetching the general question response id

            select count(1) into v_count_general_question
            from t_rvw_qnaire_response 
            where qnaire_vers_id = p_local_gen_qnaire_vers_id 
                and pidc_vers_id = f_a2l_wp_resp_id.pidc_vers_id 
                and nvl(VARIANT_ID, -1)  = nvl(f_a2l_wp_resp_id.VARIANT_ID, -1)
                and A2L_WP_ID = f_a2l_wp_resp_id.A2L_WP_ID 
                and A2L_RESP_ID = f_a2l_wp_resp_id.A2L_RESP_ID;

            pk_log.info ('v_count_general_question value identified : ' || v_count_general_question); 
            
            --only 1 general question should be found for a combination of a2l wp and resp , pidc version,variant and gen qnaire vers id
            IF v_count_general_question = 1 
            THEN 
            
                select QNAIRE_RESP_ID into v_gen_qnaire_resp_id 
                from t_rvw_qnaire_response 
                where qnaire_vers_id = p_local_gen_qnaire_vers_id 
                    and pidc_vers_id = f_a2l_wp_resp_id.pidc_vers_id 
                    and nvl(VARIANT_ID, -1)  = nvl(f_a2l_wp_resp_id.VARIANT_ID, -1)
                    and A2L_WP_ID = f_a2l_wp_resp_id.A2L_WP_ID 
                    and A2L_RESP_ID = f_a2l_wp_resp_id.A2L_RESP_ID;
                
                pk_log.info('general question resp id for the WP and RESP combination QNAIRE_RESP_ID : '|| v_gen_qnaire_resp_id);
                
                FOR f_qnaire_resp IN 
                    ( 
                        select * 
                        from t_rvw_qnaire_response 
                        where A2L_WP_ID = f_a2l_wp_resp_id.A2L_WP_ID  
                            and A2L_RESP_ID = f_a2l_wp_resp_id.A2L_RESP_ID 
                            and nvl(VARIANT_ID, -1)  = nvl(f_a2l_wp_resp_id.VARIANT_ID, -1)
                            and pidc_vers_id = f_a2l_wp_resp_id.pidc_vers_id
                    )
                LOOP
                    
                    pk_log.info ('Current Qnaire Resp ID : ' || f_qnaire_resp.QNAIRE_RESP_ID);

                    IF v_gen_qnaire_resp_id is not null and f_qnaire_resp.QNAIRE_RESP_ID != v_gen_qnaire_resp_id 
                    THEN 
                   
                        --considering the working sets answers for general question answer merging
                        select QNAIRE_RESP_VERS_ID into v_temp_qnaire_resp_ver_id from t_rvw_qnaire_resp_versions where qnaire_resp_id = f_qnaire_resp.QNAIRE_RESP_ID and REV_NUM = 0;
                        pk_log.info ('Working set version of Qnaire RESP - ' || f_qnaire_resp.QNAIRE_RESP_ID || ' is : ' || v_temp_qnaire_resp_ver_id);
                        
                        FOR f_qnaire_answer IN 
                            (select * from t_rvw_qnaire_answer where QNAIRE_RESP_VERS_ID = v_temp_qnaire_resp_ver_id) 
                        LOOP
                            
                            --checking whether there is any general question answered. count will be 0 if there is answer for any general question
                            select count(1) into gen_qes_count 
                            from t_question 
                            where qnaire_vers_id = f_qnaire_resp.qnaire_vers_id 
                                and q_id = f_qnaire_answer.q_id;
                                
                            pk_log.info ('gen_qes_count = ' || gen_qes_count);
                            
                            -- For general question, count would be zero, as the qnaire version Id is different
                            IF(gen_qes_count = 0) 
                            THEN
                                pk_log.info ('General Questions Answer Found in the Qnaire Response : ' || p_local_gen_qnaire_vers_id);
                                --to find the matching question in general questions for the current responses answer
                                pk_log.info ('Question id : '|| f_qnaire_answer.q_id || ', General Questions Number : '|| v_fetched_question.q_number  || ', Name : '|| v_fetched_question.q_name_eng);	

                                select * into v_fetched_question 
                                from t_question 
                                where q_id = f_qnaire_answer.q_id;
                                
                                pk_log.info ('v_fetched_question.q_id = ' || v_fetched_question.q_id || ', General Questions Number : '|| v_fetched_question.q_number  || ', Name : '|| v_fetched_question.q_name_eng);	
                                                
                                select count(1) into v_gen_ques_count 
                                from t_question 
                                where qnaire_vers_id = p_local_gen_qnaire_vers_id 
                                    and q_number = v_fetched_question.q_number 
                                    and q_name_eng = v_fetched_question.q_name_eng;
                                    
                                pk_log.info ('v_gen_ques_count = '|| v_gen_ques_count);
                                
                                -- if identified question count is more than 1
                                IF(v_gen_ques_count > 1 )
                                THEN
                                    
                                    pk_log.info ('identify the parent question for that incoming q id : ' || f_qnaire_answer.q_id);
                                    select * into v_parent_ques 
                                    from t_question 
                                    where q_id = (select parent_q_id  
                                                    from t_question  
                                                    where q_id = f_qnaire_answer.q_id);
                                                    
                                    pk_log.info ('v_parent_ques.q_id = ' || v_parent_ques.q_id || ', v_parent_ques.q_number : '|| v_parent_ques.q_number  || ', v_parent_ques.q_name_eng : '|| v_parent_ques.q_name_eng);	
                                                    
                                    pk_log.info ('identify the exact match parent question for the input general qnaire version id :' || p_local_gen_qnaire_vers_id);
                                    select * into v_match_parent_ques 
                                    from t_question 
                                    where qnaire_vers_id = p_local_gen_qnaire_vers_id 
                                        and q_number = v_parent_ques.q_number 
                                        and q_name_eng = v_parent_ques.q_name_eng;
                                        
                                    pk_log.info ('v_match_parent_ques.q_id = ' || v_match_parent_ques.q_id);	
                                        
                                    pk_log.info ('identify the matching question id for the current input general qnaire version id  '); 
                                    select * into v_question_gen 
                                    from t_question 
                                    where parent_q_id = v_match_parent_ques.q_id
                                        and qnaire_vers_id = p_local_gen_qnaire_vers_id
                                        and q_number = v_fetched_question.q_number 
                                        and q_name_eng = v_fetched_question.q_name_eng;
                                
                                ELSE
                                    --identify the matching question id for the current input general qnaire version id  
                                    select * into v_question_gen 
                                    from t_question 
                                    where qnaire_vers_id = p_local_gen_qnaire_vers_id 
                                        and q_number = v_fetched_question.q_number 
                                        and q_name_eng = v_fetched_question.q_name_eng;
                                
                                END IF;
                                
                                pk_log.info ('Matching gen qn fetch :  qn found - v_question_gen.q_id = ' || v_question_gen.q_id || ', v_question_gen.HEADING_FLAG = ' || v_question_gen.HEADING_FLAG);
                                        
                                IF v_question_gen.HEADING_FLAG = 'N' 
                                THEN
                                    
                                    pk_log.info ('v_fetched_question.q_id = ' || v_fetched_question.q_id || ' is NOT a heading');
                                    
                                    pk_log.info('Using v_gen_qnaire_resp_id = ' || v_gen_qnaire_resp_id || '...');
                                    select QNAIRE_RESP_VERS_ID into v_rvw_qnaire_resp_ver from t_rvw_qnaire_resp_versions where qnaire_resp_id = v_gen_qnaire_resp_id and rev_num = 0;
                                    pk_log.info ('General Qnaire Resp Version ''Working Set'' ID : ' || v_rvw_qnaire_resp_ver);

                                    --checking general question answer ids 
                                    select count(1) into rvw_ans_count  
                                    from T_RVW_QNAIRE_ANSWER 
                                    where q_id = v_question_gen.q_id 
                                        and qnaire_resp_vers_id = v_rvw_qnaire_resp_ver;
                                            
                                    pk_log.info('rvw_ans_count = ' || rvw_ans_count);	
                                    
                                    IF rvw_ans_count = 0 
                                    THEN
                                        pk_log.info ('''If answer'' is not available. insert will happen for : ' || v_question_gen.q_id);
                                        --inserting dummy answer in rvw question answer for general questionnaire
                                        INSERT INTO T_RVW_QNAIRE_ANSWER 
                                            (Q_ID,RESULT,QNAIRE_RESP_ID,QNAIRE_RESP_VERS_ID)
                                        VALUES
                                        (
                                            v_question_gen.q_id,
                                            'P',
                                            v_gen_qnaire_resp_id,
                                            v_rvw_qnaire_resp_ver
                                        );
                                        pk_log.info('Inserted T_RVW_QNAIRE_ANSWER');                   
                                    END IF; 
                                    
                                    --fetching the general question details
                                    pk_log.info('Using v_rvw_qnaire_resp_ver : ' || v_rvw_qnaire_resp_ver || ', v_question_gen.q_id = ' || v_question_gen.q_id || '...');	
                                    select * into v_rvw_gen_qnaire_answer 
                                    from T_RVW_QNAIRE_ANSWER 
                                    where q_id = v_question_gen.q_id 
                                        and qnaire_resp_vers_id = v_rvw_qnaire_resp_ver;
                                    
                                    pk_log.info ('Related General Question Id from Answer Table : '|| v_rvw_gen_qnaire_answer.q_id || ', Related General Question Id : ' || v_question_gen.q_id);
                                    pk_log.info ('Related General Question Name : '|| v_question_gen.q_name_eng);             
                                    
                                    --to fill the rvw answer comments 
                                    find_qnaire_name(f_qnaire_resp.qnaire_vers_id, v_qnaire_name);
                                    
                                    --initalize the variables with the current value
                                    v_qnaire_comment := v_rvw_gen_qnaire_answer.REMARK;
                                    v_upd_result := v_rvw_gen_qnaire_answer.RESULT;
                                    v_upd_result_opt_id := v_rvw_gen_qnaire_answer.Q_RESULT_OPT_ID;
    
                                    IF f_qnaire_answer.remark is not null 
                                    THEN
                                        --to check the length of the string
                                        
                                        v_remark_length := LENGTH(v_rvw_gen_qnaire_answer.remark||v_qnaire_name || f_qnaire_answer.remark);
                                        
                                        pk_log.info('v_remark_length = ' || v_remark_length);
                                        
                                        -- Add the comment only if less than 4000 (or, 3950)
                                        if v_remark_length < 3950
                                        then
                                            v_qnaire_comment := v_qnaire_name ||':'|| f_qnaire_answer.remark||';';
                                            IF v_rvw_gen_qnaire_answer.remark is not null 
                                            THEN
                                                v_qnaire_comment := v_rvw_gen_qnaire_answer.remark || v_qnaire_comment;
                                            END IF;
                                        
                                            pk_log.info ('remarks to be updated : ' || v_qnaire_comment );
                                        end if;
                                    END IF;
                                    
                                    pk_log.info ('old results : ' || f_qnaire_answer.RESULT );
                                    pk_log.info ('old results option Id : ' || f_qnaire_answer.Q_RESULT_OPT_ID );
                                    
                                    --result should not be null to fetch result options
                                    IF f_qnaire_answer.RESULT IS NOT NULL and f_qnaire_answer.RESULT != '?' 
                                    THEN 
                                        select * into v_gen_ques_res_opt 
                                        from t_question_result_options 
                                        where q_id =  v_question_gen.q_id 
                                            and q_result_type = f_qnaire_answer.RESULT;
                                        pk_log.info ('general result opt id : ' || v_gen_ques_res_opt.q_result_opt_id );
                                    END IF;
                                    
                                    --to update the result option if both the result options are positive 
                                    IF v_rvw_gen_qnaire_answer.RESULT = 'P' and f_qnaire_answer.RESULT = 'P' 
                                    THEN 
                                        v_upd_result_opt_id := v_gen_ques_res_opt.q_result_opt_id;
                                        v_upd_result := f_qnaire_answer.RESULT; 
                                        pk_log.info ('Updated Result : ' || f_qnaire_answer.RESULT || ', Q Result Opt Id : ' ||  v_gen_ques_res_opt.q_result_opt_id );
                                    END IF;
                                    
                                    --to update the general question results based on answered results
                                    IF v_rvw_gen_qnaire_answer.RESULT != f_qnaire_answer.RESULT or f_qnaire_answer.RESULT is null 
                                    THEN
                                        IF v_rvw_gen_qnaire_answer.RESULT = 'P' and (f_qnaire_answer.RESULT = 'N' or f_qnaire_answer.RESULT = '?' or f_qnaire_answer.RESULT is null) 
                                        THEN
                                            IF f_qnaire_answer.RESULT is null or f_qnaire_answer.RESULT = '?' 
                                            THEN
                                                pk_log.info ('Update Result to : null' );
                                                --update the answer will null result and result opt id
                                                v_upd_result_opt_id := null;
                                                v_upd_result := null; 
                                            ELSE
                                                --update the answer will new result and result opt id
                                                v_upd_result_opt_id := v_gen_ques_res_opt.q_result_opt_id;
                                                v_upd_result := f_qnaire_answer.RESULT; 
                                                pk_log.info ('Updated Result : '|| f_qnaire_answer.RESULT || ', Q Result Opt Id : ' ||  v_gen_ques_res_opt.q_result_opt_id   );
                                            END IF;
                                            
                                        ELSIF v_rvw_gen_qnaire_answer.RESULT = 'N' and (f_qnaire_answer.RESULT is null or f_qnaire_answer.RESULT = '?') 
                                        THEN
                                            pk_log.info ('Updated Result  null');
                                            --update the answer with null result and result opt id
                                            v_upd_result_opt_id := null;
                                            v_upd_result := null; 
                                        
                                        END IF;
                                    END IF;
                                    
                                    pk_log.info ('Updating T_RVW_QNAIRE_ANSWER for rvw_answer_id ' || v_rvw_gen_qnaire_answer.rvw_answer_id);
                                    UPDATE T_RVW_QNAIRE_ANSWER 
                                    set RESULT = v_upd_result,
                                        Q_RESULT_OPT_ID = v_upd_result_opt_id ,
                                        remark = v_qnaire_comment
                                    where rvw_answer_id = v_rvw_gen_qnaire_answer.rvw_answer_id;
                                    
                                    select RESULT into v_temp_print_result from T_RVW_QNAIRE_ANSWER where rvw_answer_id = v_rvw_gen_qnaire_answer.rvw_answer_id;
                                    
                                    pk_log.info ('Updated Result for answer is : '|| v_temp_print_result || ', Answer ID : ' || v_rvw_gen_qnaire_answer.rvw_answer_id);
                                    
                                    --Adding links to the new rvw answer
                                    FOR f_answer_links IN 
                                        (select *  from t_links where node_id = f_qnaire_answer.rvw_answer_id ) 
                                    LOOP
                                
                                        select count(1) into v_link_already_found 
                                        from t_links 
                                        where node_id = v_rvw_gen_qnaire_answer.rvw_answer_id 
                                            and NODE_TYPE = f_answer_links.NODE_TYPE 
                                            and DESC_ENG = f_answer_links.DESC_ENG;
                                            
                                        If v_link_already_found = 0 
                                        Then 
                                            INSERT INTO T_LINKS
                                                (NODE_ID, NODE_TYPE, LINK_URL, DESC_ENG, DESC_GER)
                                            VALUES
                                            (
                                                v_rvw_gen_qnaire_answer.rvw_answer_id,
                                                f_answer_links.NODE_TYPE,
                                                f_answer_links.LINK_URL,
                                                f_answer_links.DESC_ENG,
                                                f_answer_links.DESC_GER
                                            );
                                            pk_log.info('Inserted Link : ' || f_answer_links.LINK_URL);
                                        End if;

                                    END LOOP;
                                END IF;
                            
                            END IF;                 
                        END LOOP;
                    
                    END IF;
                END LOOP;
            
            END IF;


            -- Set the flag to completed
            update t_rvw_qnaire_response 
            set MIG_STATE = 'C' 
            where   A2L_WP_ID = f_a2l_wp_resp_id.A2L_WP_ID 
                and A2L_RESP_ID = f_a2l_wp_resp_id.A2L_RESP_ID 
                and nvl(VARIANT_ID, -1)  = nvl(f_a2l_wp_resp_id.VARIANT_ID, -1)
                and pidc_vers_id = f_a2l_wp_resp_id.pidc_vers_id;
            
            
            v_commiter_counter := v_commiter_counter + 1;
            
            --batch commit
            if v_commiter_counter >= cBATCH_COMMIT_SIZE
            then
                pk_log.info('Batch commit');
                commit;
                v_commiter_counter :=0;
            end if;
            
        END LOOP;
        
        commit;
        pk_log.info('Final commit');
        
    EXCEPTION 
        when others then
            PK_LOG.ERROR('Error inside merge general questionnaire job', sqlcode, sqlerrm); 
            PK_LOG.END_JOB;
            RAISE_APPLICATION_ERROR(-20000, 'Error inside merge general questionnaire job');
    END;


--*************************************************
-- MAIN
--*************************************************
BEGIN 
    PK_LOG.START_NEW_JOB('Step4_Questionnaire_Data_Migration_Job');
    
    --getting general questionnaire version id
    select param_value into v_general_ques_id from tabv_common_params where param_id = 'GENERAL_QNAIRE_ID';   
        
    select qnaire_vers_id into v_gen_qnaire_vers_id from t_questionnaire_version where qnaire_id = v_general_ques_id and active_flag = 'Y';

    pk_log.info('Active General Questionnaire Version ID : ' || v_gen_qnaire_vers_id);

    --calling merge general questionnaire answers to new general question answer
    pk_log.info('Merging of General Questions answers based on WP and RESP');

    merge_general_question_answer(v_general_ques_id, v_gen_qnaire_vers_id);

    pk_log.info('Completed Merging of General Questions answers based on WP and RESP');

    PK_LOG.END_JOB;

EXCEPTION 
    when others then
        PK_LOG.ERROR('Error in Step 4 Merging of general question', sqlcode, sqlerrm); 
        PK_LOG.END_JOB;
        RAISE_APPLICATION_ERROR(-20000, 'Error in Step 4 Merging of general question '); 
end; 
/

spool off
