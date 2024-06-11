spool c:\temp\32_QnaireMig_Step3C-DoNotRunUnlessIssuesFoundByValidationScript2.log


----------------------------------------------
--Fixing Script for Step 3 - Validation 2
----------------------------------------------
----------------------------------------------
--Script to create general questions for questionnaire versions 
--Note : This script has to be run only if there are any pidc versions that dont have merged questions for a specific a2l wp and resp
----------------------------------------------

----------------------------------------------
--Migration script to create mertged general and existing question if not created in a specific pidc version
--Note : this script has to be ran for a specific pidc versions
--identify the affected pidc version and run this script to create merged question versions
--The Script won't create megred questionnaire versions if the questionnaire response already have it
----------------------------------------------

WHENEVER SQLERROR EXIT SQL.SQLCODE ROLLBACK;

declare 
	--input pidc version id
    v_input_pidc_version number := <pidc_vers_id>;
    
    v_merged_version_found number := 0;
	
	v_general_ques_id number;
    
    v_total_counter number := 0;
    
    v_gen_ques_found number := 0;
    
    --procedure to create qnaire resp versions and its associatted tables
    PROCEDURE create_spec_qnaire_resp_vers
        (t_qnaire_resp_id IN NUMBER,t_new_qnaire_resp_id IN NUMBER) 
    IS
    
        v_temp_qnaire_resp_vers_id T_RVW_QNAIRE_RESP_VERSIONS.QNAIRE_RESP_VERS_ID%type;
        v_temp_qnaire_answer_id T_RVW_QNAIRE_ANSWER.RVW_ANSWER_ID%type;
        
        v_question_version T_QUESTIONNAIRE_VERSION%ROWTYPE;
        v_question T_QUESTION%ROWTYPE;
        v_question_res_opt T_QUESTION_RESULT_OPTIONS%ROWTYPE;
        v_question_res_opt_id number :=null;
		
		v_rvw_qnaire_resp_version T_RVW_QNAIRE_RESP_VERSIONS%ROWTYPE;
		
		v_count number;
		
		v_rvw_answer_found number;
		
		v_result_opt_count number;
		
		v_question_id number;
		
		max_rev_num number;
		
		v_gen_question T_QUESTION%ROWTYPE;
		
		v_temp_question_res_opt T_QUESTION_RESULT_OPTIONS%ROWTYPE;

    BEGIN 
		pk_log.info('Started Creation of Special Questionnaire Response Versions');
        
        --storing questionnaire response versions rows value
        select * into v_rvw_qnaire_resp_version 
        from T_RVW_QNAIRE_RESP_VERSIONS 
        where qnaire_resp_id = t_qnaire_resp_id and REV_NUM = 0;
        
        select count(1) into v_count from T_QUESTIONNAIRE_VERSION WHERE RELATED_QNAIRE_VERS_ID = v_rvw_qnaire_resp_version.QNAIRE_VERS_ID;			

        select * into v_question_version from T_QUESTIONNAIRE_VERSION WHERE RELATED_QNAIRE_VERS_ID = v_rvw_qnaire_resp_version.QNAIRE_VERS_ID;
        
        --Insert into T_RVW_QNAIRE_RESP_VERSIONS
        
        select max(REV_NUM) into max_rev_num from T_RVW_QNAIRE_RESP_VERSIONS where qnaire_resp_id = t_qnaire_resp_id;
        
        INSERT INTO T_RVW_QNAIRE_RESP_VERSIONS 
            (QNAIRE_RESP_ID,NAME,DESCRIPTION,REV_NUM,QNAIRE_VERS_ID,QNAIRE_VERS_STATUS,REVIEWED_DATE,REVIEWED_USER)
        VALUES
        (
            t_new_qnaire_resp_id,
            'Migrated version with both general and exisiting question',
            'Migrated version with both general and exisiting question',
            max_rev_num+1,
            v_question_version.QNAIRE_VERS_ID,
            v_rvw_qnaire_resp_version.QNAIRE_VERS_STATUS,
            sys_extract_utc(systimestamp),
            USER
        ) RETURNING QNAIRE_RESP_VERS_ID INTO v_temp_qnaire_resp_vers_id ;
             
        --to iterate all the rvw answers to create a copy of review answer
        FOR f_rvw_answer IN 
            (select * from T_RVW_QNAIRE_ANSWER where QNAIRE_RESP_VERS_ID = v_rvw_qnaire_resp_version.QNAIRE_RESP_VERS_ID) 
        LOOP
            
            select count(1) into v_count 
            from T_QUESTION 
            where RELATED_Q_ID = f_rvw_answer.Q_ID and qnaire_vers_id = v_question_version.QNAIRE_VERS_ID;
            
            pk_log.info('Match Question count  '||v_count);
            
            IF v_count = 0 THEN
                select * into v_gen_question from T_QUESTION where Q_ID = f_rvw_answer.Q_ID;
                
                select * into v_question from T_QUESTION where q_number = v_gen_question.q_number and q_name_eng = v_gen_question.q_name_eng and qnaire_vers_id = v_question_version.QNAIRE_VERS_ID;
                pk_log.info('Found the question for existing version ' || v_question.RELATED_Q_ID);
            
            ELSE
                pk_log.info('Rvw Answer ID '|| f_rvw_answer.RVW_ANSWER_ID || ' QNAIRE_RESP VERSION ID ' || v_question_version.QNAIRE_VERS_ID);
                pk_log.info('Answers Question ID  '|| f_rvw_answer.Q_ID || ' Count ' || v_count);
                
                --fetch the latest question id based on related q id     
                select * into v_question 
                from T_QUESTION 
                where RELATED_Q_ID = f_rvw_answer.Q_ID and qnaire_vers_id = v_question_version.QNAIRE_VERS_ID;
                
            END IF;
            
            v_question_id := v_question.Q_ID;
            pk_log.info('Result Option value  '|| f_rvw_answer.Q_RESULT_OPT_ID || ' Question Id '|| v_question_id);
            
            if f_rvw_answer.Q_RESULT_OPT_ID is not null 
            then
            
                --fetch the latest question result option id based on related q id 
                select count(1) into v_result_opt_count 
                from T_QUESTION_RESULT_OPTIONS 
                    where RELATED_Q_RESULT_OPT_ID = f_rvw_answer.Q_RESULT_OPT_ID
                    and q_id = v_question_id;
                pk_log.info('Related Result Option found '|| v_result_opt_count);
                
                IF v_result_opt_count = 0 THEN
                    select * into v_temp_question_res_opt 
                    from T_QUESTION_RESULT_OPTIONS 
                        where Q_RESULT_OPT_ID = f_rvw_answer.Q_RESULT_OPT_ID;
                            
                    select * into v_question_res_opt 
                    from T_QUESTION_RESULT_OPTIONS 
                    where q_id = v_question_id 
                        and Q_RESULT_TYPE = v_temp_question_res_opt.Q_RESULT_TYPE 
                        and Q_RESULT_NAME = v_temp_question_res_opt.Q_RESULT_NAME;
                
                ELSE
                    select * into v_question_res_opt 
                    from T_QUESTION_RESULT_OPTIONS 
                    where RELATED_Q_RESULT_OPT_ID = f_rvw_answer.Q_RESULT_OPT_ID
                        and q_id = v_question_id;
                END IF;
                
                v_question_res_opt_id := v_question_res_opt.Q_RESULT_OPT_ID;
                pk_log.info('Question Result Option ID  '|| v_question_res_opt_id);
                
            end if;
            
            --Insert into T_RVW_QNAIRE_ANSWER
            INSERT INTO T_RVW_QNAIRE_ANSWER 
                (Q_ID,RESULT,MEASUREMENT,SERIES,REMARK,Q_RESULT_OPT_ID,QNAIRE_RESP_ID,QNAIRE_RESP_VERS_ID)
            VALUES
            (
                v_question_id,
                f_rvw_answer.RESULT,
                f_rvw_answer.MEASUREMENT,
                f_rvw_answer.SERIES,
                f_rvw_answer.REMARK,
                v_question_res_opt_id,
                t_new_qnaire_resp_id,
                v_temp_qnaire_resp_vers_id
            ) RETURNING RVW_ANSWER_ID into v_temp_qnaire_answer_id;
            
            FOR f_rvw_answer_opl IN 
                (select * from T_RVW_QNAIRE_ANSWER_OPL where RVW_ANSWER_ID = f_rvw_answer.rvw_answer_id) 
            LOOP                   
            
                --Insert into T_RVW_QNAIRE_ANSWER_OPL
                INSERT INTO T_RVW_QNAIRE_ANSWER_OPL 
                    (OPEN_POINTS,MEASURE,RESPONSIBLE,COMPLETION_DATE,RESULT,RVW_ANSWER_ID)
                VALUES
                (
                    f_rvw_answer_opl.OPEN_POINTS,
                    f_rvw_answer_opl.MEASURE,
                    f_rvw_answer_opl.RESPONSIBLE,
                    f_rvw_answer_opl.COMPLETION_DATE,
                    f_rvw_answer_opl.RESULT,
                    v_temp_qnaire_answer_id
                );
            END LOOP;

            FOR f_t_links IN 
                (select * from T_LINKS where NODE_ID = f_rvw_answer.rvw_answer_id) 
            LOOP
                
                --Insert into T_Links table 
                INSERT INTO T_LINKS
                    (NODE_ID,NODE_TYPE,LINK_URL,DESC_ENG,DESC_GER)
                VALUES
                (
                    v_temp_qnaire_answer_id,
                    f_t_links.NODE_TYPE,
                    f_t_links.LINK_URL,
                    f_t_links.DESC_ENG,
                    f_t_links.DESC_GER
                );
            END LOOP;
            
        END LOOP; 
        
        --Adding Review Answers for the question that not available in the source questionniare response
        --later the non relevent review answers will be removed using a java logic
        FOR f_question IN 
            (select * from T_QUESTION where QNAIRE_VERS_ID = v_question_version.QNAIRE_VERS_ID) 
        LOOP
        
            select count(1) into v_rvw_answer_found 
            from T_RVW_QNAIRE_ANSWER 
            where q_id = f_question.q_id and QNAIRE_RESP_VERS_ID = v_temp_qnaire_resp_vers_id;
            
            IF v_rvw_answer_found = 0 
            THEN
                INSERT INTO T_RVW_QNAIRE_ANSWER 
                    (Q_ID,QNAIRE_RESP_ID,QNAIRE_RESP_VERS_ID)
                VALUES
                (
                    f_question.Q_ID,
                    t_new_qnaire_resp_id,
                    v_temp_qnaire_resp_vers_id
                ) ;
                
                pk_log.info('Rvw Answer Inserted for the Question '|| f_question.q_id);
            ELSE
                pk_log.info('Rvw Answer Already Availabel for the Question '|| f_question.q_id);
            END IF;
        END LOOP;
        
        pk_log.info('Completed Creation of Special Questionnaire Response Versions');
            
	EXCEPTION
        WHEN OTHERS THEN
            PK_LOG.ERROR('Error in Creation of Special Questionnaire Response Versions : ',sqlcode,sqlerrm); 
            PK_LOG.END_JOB;
            RAISE_APPLICATION_ERROR(-20000, 'Error in Creation of Special Questionnaire Response Versions'); 
    END;
    
    
begin 
    PK_LOG.START_NEW_JOB('Step3C_Fixing_script_for_merged_version');
    
    --getting general questionnaire id
    select param_value into v_general_ques_id 
    from tabv_common_params 
    where param_id = 'GENERAL_QNAIRE_ID';
    
    FOR v_qnaire_resp_id IN 
        (select * from t_rvw_qnaire_response where pidc_vers_id = v_input_pidc_version) 
    LOOP
        select count(1) into v_gen_ques_found 
        from t_questionnaire_version 
        where  qnaire_id = v_general_ques_id and qnaire_vers_id = v_qnaire_resp_id.qnaire_vers_id;
        
        --below special versions creation should not be created for general question version
        if v_gen_ques_found = 0 
        then
            select count(1) into v_merged_version_found 
            from t_rvw_qnaire_resp_versions 
            where qnaire_resp_id = v_qnaire_resp_id.qnaire_resp_id and name like '%Migrated version with both general and exisiting question%';
            
            if v_merged_version_found = 0 
            then
                v_total_counter := v_total_counter +1;
                pk_log.info('Creating special Version for qnaire resp '|| v_qnaire_resp_id.qnaire_resp_id || ' Counter value '|| v_total_counter);
                
                create_spec_qnaire_resp_vers(v_qnaire_resp_id.qnaire_resp_id,v_qnaire_resp_id.qnaire_resp_id);
            end if;
            
        end if;
        
    END LOOP;
    
    pk_log.info('Total Special Versions created '|| v_total_counter);
    PK_LOG.END_JOB;
    
EXCEPTION
    WHEN OTHERS THEN
        PK_LOG.ERROR('Error in Creation of Special Questionnaire Response Versions : ',sqlcode,sqlerrm); 
		PK_LOG.END_JOB;
		RAISE_APPLICATION_ERROR(-20000, 'Error in Creation of Special Questionnaire Response Versions'); 
end ;
/

spool off
