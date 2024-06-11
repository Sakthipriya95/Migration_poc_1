spool c:\temp\32_QnaireMig_Step1A-MergedQnaireDefVerCreation.log

--------------------------------------------------------------------------------------------------
-- Script is to create new Q-naire def version for all qnaires, with both qnaire and gen questions
--
-- IMPORTANT : before commiting, run the next validation script(Step1B_Validations.sql) !!!
--------------------------------------------------------------------------------------------------

set serveroutput on size unlimited

WHENEVER SQLERROR EXIT SQL.SQLCODE ROLLBACK;

declare 
    qnaire_version T_QUESTIONNAIRE_VERSION%ROWTYPE;
	
    max_vers number;
   
    v_temp_qnaire_vers_id number;
    
	v_temp_question_number number;
	
	v_temp_gen_qnaire_vers_id number;

	
    --Start for Questionnaire Resposne Migration
	rel_qnaire_ver_count number;
            
    v_general_ques_id number;
	
	v_qnaire_def_ver_counter number;
  
	--icdm:pidvid,1423099081
	--Input PIDC VERSIon
	--V_temp_test_pidc_vers_id number:=14452963331;
	
	--cursor to Creating New Questionnaire Definition Versions with General and Existing questions
    CURSOR qnaire_vers_cur IS 
		select * 
		from T_QUESTIONNAIRE_VERSION 
		where qnaire_vers_id in (select distinct qnaire_vers_id 
								from t_rvw_qnaire_response 
								--where pidc_vers_id = V_temp_test_pidc_vers_id
							);
        
    
    --Start for creating new questionnaire version using general and existing question
    
    --- procedure to add question and its dependent tables data
    PROCEDURE add_question_and_dep_tables(qnaire_vers_id IN number,qes_id IN number,v_parent_ques_id IN number,v_q_no IN NUMBER,new_ques_id OUT number)
    IS
		v_question_config T_QUESTION_CONFIG%ROWTYPE;
		v_question T_QUESTION%ROWTYPE;
		v_temp_q_id number;
		v_temp_q_attr_dep_id number;
		
		v_ques_config_count number;
    
    begin
		pk_log.info('Question Id: ' || qes_id);
		
		select * into v_question from T_QUESTION where q_id = qes_id;
		pk_log.info('Question Name: ' || v_question.Q_NAME_ENG  || ' Question Number: ' || v_q_no);
		
        --Insert into question
        INSERT INTO T_QUESTION (QNAIRE_VERS_ID,Q_NUMBER,Q_NAME_ENG,Q_NAME_GER,Q_HINT_ENG,Q_HINT_GER,HEADING_FLAG,PARENT_Q_ID,
			DELETED_FLAG,POSITIVE_RESULT,RESULT_RELEVANT_FLAG,RELATED_Q_ID) 
        VALUES
            (
                qnaire_vers_id,
                v_q_no,
                v_question.Q_NAME_ENG,
                v_question.Q_NAME_GER,
                v_question.Q_HINT_ENG,
                v_question.Q_HINT_GER,
                v_question.HEADING_FLAG,
                v_parent_ques_id,
                v_question.DELETED_FLAG,
                v_question.POSITIVE_RESULT,
                v_question.RESULT_RELEVANT_FLAG,
                qes_id
            ) RETURNING Q_ID INTO new_ques_id;
		
		--to load the question confiuration
		select count(1) into v_ques_config_count from T_QUESTION_CONFIG where q_id = qes_id;
		IF v_ques_config_count > 0 THEN
			select * into v_question_config from T_QUESTION_CONFIG where q_id = qes_id;
			-- insert into question configuration
			INSERT INTO T_QUESTION_CONFIG 
				(Q_ID,RESULT,MEASUREMENT,SERIES,LINK,OPEN_POINTS,REMARK,MEASURE,RESPONSIBLE,COMPLETION_DATE) 
				VALUES
					(
						new_ques_id,
						v_question_config.RESULT,
						v_question_config.MEASUREMENT,
						v_question_config.SERIES,
						v_question_config.LINK,
						v_question_config.OPEN_POINTS,
						v_question_config.REMARK,
						v_question_config.MEASURE,
						v_question_config.RESPONSIBLE,
						v_question_config.COMPLETION_DATE
					);
		END IF;
		
		--to loop and insert question result option
		FOR v_ques_result_option IN (select * from T_QUESTION_RESULT_OPTIONS where Q_ID= qes_id) 
		LOOP
			INSERT INTO T_QUESTION_RESULT_OPTIONS 
				(Q_ID,Q_RESULT_NAME,Q_RESULT_TYPE,RELATED_Q_RESULT_OPT_ID)
				VALUES
					(
						new_ques_id,
						v_ques_result_option.Q_RESULT_NAME,
						v_ques_result_option.Q_RESULT_TYPE,
						v_ques_result_option.Q_RESULT_OPT_ID
					);
		END LOOP;
            
		--to loop and insert question depn attr      
		FOR v_ques_dep_attr IN (select * from T_QUESTION_DEPEN_ATTRIBUTES where Q_ID = qes_id) 
		LOOP
			INSERT INTO T_QUESTION_DEPEN_ATTRIBUTES 
				(Q_ID,ATTR_ID)
				VALUES
					(
						new_ques_id,
						v_ques_dep_attr.ATTR_ID
					)RETURNING QATTR_DEPEN_ID INTO v_temp_q_attr_dep_id;
			--to loop and insert question depn attr values        
			FOR v_ques_dep_attr_value IN (select * from T_QUESTION_DEPEN_ATTR_VALUES where Q_ATTR_DEP_ID = v_ques_dep_attr.QATTR_DEPEN_ID) 
			LOOP
				INSERT INTO T_QUESTION_DEPEN_ATTR_VALUES 
					(Q_ATTR_DEP_ID,Q_COMBI_NUM,VALUE_ID)
					VALUES
						(
							v_temp_q_attr_dep_id,
							v_ques_dep_attr_value.Q_COMBI_NUM,
							v_ques_dep_attr_value.VALUE_ID
						);
		   END LOOP;
		END LOOP;
	
	EXCEPTION
        WHEN OTHERS THEN
			PK_LOG.ERROR('Error in add_question_and_dep_tables : ',sqlcode,sqlerrm); 
			PK_LOG.END_JOB;
			RAISE_APPLICATION_ERROR(-20000, 'Error in add_question_and_dep_tables'); 
    END;
	
	--procedure to create new questions based on the parent child order
    PROCEDURE create_ques_for_new_version(qnaire_vers_id IN NUMBER , v_parent_q_id IN NUMBER,v_q_number IN NUMBER) IS 
        v_temp_quest_1_id number;
        v_temp_quest_2_id number;
        v_temp_quest_3_id number;
		v_temp_ques_number number;
		
		v_temp_lvl2_count number;
    BEGIN
		v_temp_quest_1_id := null;
		pk_log.info('Lvl 1 Question Started ');
        --CREATING LVL 1 QUESTION AND STORING THE QUESTION ID IN v_temp_quest_1_id
        add_question_and_dep_tables(qnaire_vers_id,v_parent_q_id,null,v_q_number,v_temp_quest_1_id);
		--select count(1) into v_temp_lvl2_count from T_QUESTION where PARENT_Q_ID = v_parent_q_id order by q_number;
		--pk_log.info('parent Question ID ' || v_parent_q_id || ' count of level  2 questions '|| v_temp_lvl2_count);
		pk_log.info('Lvl 1 Question Added ' || v_temp_quest_1_id );
        FOR v_ques_child_lvl1 IN (select * from T_QUESTION where PARENT_Q_ID = v_parent_q_id order by q_number) LOOP
			pk_log.info('Lvl 2 Question Started for question number '|| v_ques_child_lvl1.Q_NUMBER );
			v_temp_ques_number := v_ques_child_lvl1.Q_NUMBER;
            --CREATING LVL 2 QUESTION AND STORING THE QUESTION ID IN v_temp_quest_2_id
            add_question_and_dep_tables(qnaire_vers_id,v_ques_child_lvl1.q_id,v_temp_quest_1_id,v_temp_ques_number,v_temp_quest_2_id);
			pk_log.info('Lvl 2 Question Added ' || v_temp_quest_2_id );
                FOR v_ques_child_lvl2 IN (select * from T_QUESTION where PARENT_Q_ID = v_ques_child_lvl1.q_id order by q_number) LOOP
					pk_log.info('Lvl 3 Question Started for question number '|| v_ques_child_lvl2.Q_NUMBER );
					v_temp_ques_number := v_ques_child_lvl2.Q_NUMBER;
                    --CREATING LVL 3 QUESTION AND STORING THE QUESTION ID IN v_temp_quest_3_id
                    add_question_and_dep_tables(qnaire_vers_id,v_ques_child_lvl2.q_id,v_temp_quest_2_id,v_temp_ques_number,v_temp_quest_3_id);
					pk_log.info('Lvl 3 Question Added ' || v_temp_quest_3_id );
                END LOOP;   
        END LOOP;
	EXCEPTION
        WHEN OTHERS THEN
        PK_LOG.ERROR('Error in create_ques_for_new_version : ',sqlcode,sqlerrm); 
		PK_LOG.END_JOB;
        RAISE_APPLICATION_ERROR(-20000, 'Error in create_ques_for_new_version');
    END;
	
	--procedure to update teh dependency object id with the latest question id
    PROCEDURE update_dep_objId_in_question(rel_qes_id IN NUMBER,v_qnaire_vers_id IN number ) IS
        v_ques_up_dep T_QUESTION%ROWTYPE;
        v_new_dep_question T_QUESTION%ROWTYPE;
        v_new_dep_ques_res_opt T_QUESTION_RESULT_OPTIONS.Q_RESULT_OPT_ID%TYPE;
		
		v_new_current_question T_QUESTION%ROWTYPE;
		
		--v_qestion_count number;
		--v_dep_count number;
		--v_result_opt_count number;
    BEGIN 
        --checks if the related question is not null
		pk_log.info('rel_qes_id  is '|| rel_qes_id);
        if rel_qes_id is not null then
		
			select * into v_new_current_question from T_QUESTION where RELATED_Q_ID = rel_qes_id and QNAIRE_VERS_ID = v_qnaire_vers_id;
				
            --fetches the related question using the related question id
			--select count(*) into v_qestion_count from T_QUESTION where q_id = rel_qes_id;
			--pk_log.info('Question count '|| v_qestion_count);
            select * into v_ques_up_dep from T_QUESTION where q_id = rel_qes_id;
            --related questions dep question id should not be null
			--pk_log.info('DEP_QUES_ID  is'|| v_ques_up_dep.DEP_QUES_ID);
            if  v_ques_up_dep.DEP_QUES_ID is not null then 
				pk_log.info('DEP_QUES_ID is available ');
                --fetch the new depened question id using the related question id column
				--select count(*) into v_dep_count from T_QUESTION where RELATED_Q_ID =  v_ques_up_dep.DEP_QUES_ID and QNAIRE_VERS_ID = v_qnaire_vers_id;
				--pk_log.info('Dep Question Count '|| v_dep_count || ' QNAIRE_VERS_ID' ||v_qnaire_vers_id);
				--pk_log.info(' Related Q_id '||v_ques_up_dep.DEP_QUES_ID);
                select * into v_new_dep_question from T_QUESTION where RELATED_Q_ID =  v_ques_up_dep.DEP_QUES_ID and QNAIRE_VERS_ID = v_qnaire_vers_id;
				--pk_log.info('Question count '|| v_qestion_count);
                if v_ques_up_dep.DEP_Q_RESULT_OPT_ID is not null then
					pk_log.info('Went for updating result option id');
                     --fetch the new T_QUESTION_RESULT_OPTIONS using the RELATED_Q_RESULT_OPT_ID column
					 --select count(*) into v_result_opt_count from T_QUESTION_RESULT_OPTIONS where RELATED_Q_RESULT_OPT_ID =  v_ques_up_dep.DEP_Q_RESULT_OPT_ID;
					 --pk_log.info('Question result option count '|| v_result_opt_count);
                    select Q_RESULT_OPT_ID into v_new_dep_ques_res_opt from T_QUESTION_RESULT_OPTIONS where RELATED_Q_RESULT_OPT_ID =  v_ques_up_dep.DEP_Q_RESULT_OPT_ID;
                    --update the new question with latest Q_RESULT_OPT_ID
                    --update T_QUESTION 
                    --    set DEP_Q_RESULT_OPT_ID = v_new_dep_ques_res_opt
                    --        where q_id =qes_id;
					
                end if;
				pk_log.info('Updated message started ');
				--update the new question as dep question id 
                update T_QUESTION 
                    set DEP_QUES_ID = v_new_dep_question.Q_ID,
                    DEP_QUES_RESP = v_ques_up_dep.DEP_QUES_RESP,
					DEP_Q_RESULT_OPT_ID = v_new_dep_ques_res_opt					
                        where q_id =v_new_current_question.q_id;
				
				--pk_log.info('Updated the Dependency Question  ID '|| qes_id || '  DEP_QUES_RESP '|| v_ques_up_dep.DEP_QUES_RESP );
				--pk_log.info(' Dep Question Id '|| v_new_dep_question.Q_ID);
				--pk_log.info('Updated Question Dependency Result Option ID  '||v_new_dep_ques_res_opt);
            end if;
			pk_log.info('DEP_QUES_ID  not available');
        end if;
	EXCEPTION
        WHEN OTHERS THEN
			PK_LOG.ERROR('Error in update_dep_objId_in_question : ',sqlcode,sqlerrm); 
			PK_LOG.END_JOB;
			RAISE_APPLICATION_ERROR(-20000, 'Error in update_dep_objId_in_question');
    END;
	
	--procedure to create new questions based on the parent child order
    PROCEDURE update_ques_dep_for_new_vers(qnaire_vers_id IN NUMBER , v_parent_q_id IN NUMBER) IS 	
    BEGIN
		pk_log.info('Update Lvl 1 Question depn Started ');
        --to update the dep obj id to the newly created dep obj id
        update_dep_objId_in_question(v_parent_q_id,qnaire_vers_id);
		--pk_log.info('parent Question ID ' || v_parent_q_id || ' count of level  2 questions '|| v_temp_lvl2_count);
        FOR v_ques_child_lvl1 IN (select * from T_QUESTION where PARENT_Q_ID = v_parent_q_id order by q_number) LOOP
			pk_log.info('Update Lvl 2 Question depen Started for question number '|| v_ques_child_lvl1.Q_NUMBER );
			--to update the dep obj id to the newly created dep obj id
            update_dep_objId_in_question(v_ques_child_lvl1.q_id,qnaire_vers_id);
                FOR v_ques_child_lvl2 IN (select * from T_QUESTION where PARENT_Q_ID = v_ques_child_lvl1.q_id order by q_number) LOOP
					pk_log.info('Update Lvl 3 Question depen Started for question number '|| v_ques_child_lvl2.Q_NUMBER );
                    update_dep_objId_in_question(v_ques_child_lvl2.q_id,qnaire_vers_id);
                END LOOP;   
        END LOOP;
	EXCEPTION
        WHEN OTHERS THEN
			PK_LOG.ERROR('Error in update_ques_dep_for_new_vers : ',sqlcode,sqlerrm); 
			PK_LOG.END_JOB;
			RAISE_APPLICATION_ERROR(-20000, 'Error in update_ques_dep_for_new_vers');
    END;
	
    
    PROCEDURE find_general_qnaire_version(p_qnaire_vers_id IN NUMBER, v_general_ques_id IN NUMBER, p_gen_qnaire_vers_id OUT NUMBER) IS 
        --if count is 0 then loop will be exit
        q_count number := 1;
    BEGIN
        FOR f_qnaire_resp in (select * from t_rvw_qnaire_response where QNAIRE_VERS_ID = p_qnaire_vers_id) 
		LOOP
            FOR f_qnaire_answer_val in (select * from t_rvw_qnaire_answer where qnaire_resp_id = f_qnaire_resp.qnaire_resp_id) 
			LOOP
                select count(1) into q_count from t_question where qnaire_vers_id = f_qnaire_resp.qnaire_vers_id and q_id = f_qnaire_answer_val.q_id;
                if(q_count = 0) then
                    select qnaire_vers_id into p_gen_qnaire_vers_id from t_question where q_id = f_qnaire_answer_val.q_id;
                    exit;
                end if;
            END LOOP; 
            if(q_count = 0) then
                exit;
            end if;
        END LOOP;
        IF(p_gen_qnaire_vers_id IS null) THEN
            select qnaire_vers_id 
				into p_gen_qnaire_vers_id 
			from T_QUESTIONNAIRE_VERSION 
			where qnaire_id = v_general_ques_id and active_flag ='Y';
        END IF;
    END;
    --END for creating new questionnaire version using general and existing question
	
	
BEGIN 
	PK_LOG.START_NEW_JOB('Step1_Questionnaire_Data_Migration_Job');
    --getting general questionnaire version id
    select param_value into v_general_ques_id from tabv_common_params where param_id = 'GENERAL_QNAIRE_ID';
    
    -- Start for creating new questionnaire version using general and existing question
	pk_log.info('Started Creating New Questionnaire Definition Versions with General and Existing questions');
	
    open qnaire_vers_cur;
    LOOP
		BEGIN
			FETCH qnaire_vers_cur into qnaire_version ; 
			EXIT WHEN qnaire_vers_cur%notfound;
			
			SELECT max(MAJOR_VERSION_NUM) into max_vers FROM t_questionnaire_version where qnaire_id = qnaire_version.qnaire_id;
			max_vers := max_vers + 1;
			
			select count(1) into rel_qnaire_ver_count  from T_QUESTIONNAIRE_VERSION WHERE RELATED_QNAIRE_VERS_ID =qnaire_version.qnaire_vers_id;
			pk_log.info('Count rel_qnaire_ver_count :'|| rel_qnaire_ver_count);
			pk_log.info('Questionnaire Definition Versions  :'|| qnaire_version.qnaire_vers_id || ' Questionniare Id '||qnaire_version.qnaire_id);
			
			IF rel_qnaire_ver_count = 0 THEN
				pk_log.info('Creating Questionnaire Definition Versions for Questionniare :'|| qnaire_version.qnaire_id);
				pk_log.info('Questionnaire Definition Versions  :'|| qnaire_version.qnaire_vers_id);
				
				--insert to questionnaire version 
				INSERT INTO T_QUESTIONNAIRE_VERSION (
					QNAIRE_ID,ACTIVE_FLAG,INWORK_FLAG,RESULT_RELEVANT_FLAG,RESULT_HIDDEN_FLAG,MEASUREMENT_RELEVANT_FLAG,MEASUREMENT_HIDDEN_FLAG,
					SERIES_RELEVANT_FLAG,SERIES_HIDDEN_FLAG,LINK_RELEVANT_FLAG,LINK_HIDDEN_FLAG,OPEN_POINTS_RELEVANT_FLAG,OPEN_POINTS_HIDDEN_FLAG,
					REMARK_RELEVANT_FLAG,REMARKS_HIDDEN_FLAG,MAJOR_VERSION_NUM,MINOR_VERSION_NUM,DESC_ENG,DESC_GER,MEASURE_RELAVENT_FLAG,
					MEASURE_HIDDEN_FLAG,RESPONSIBLE_RELAVENT_FLAG,RESPONSIBLE_HIDDEN_FLAG,COMPLETION_DATE_RELAVENT_FLAG,COMPLETION_DATE_HIDDEN_FLAG,RELATED_QNAIRE_VERS_ID) 
				VALUES(
						qnaire_version.qnaire_id,
						'N',
						'N',
						qnaire_version.RESULT_RELEVANT_FLAG,
						qnaire_version.RESULT_HIDDEN_FLAG,
						qnaire_version.MEASUREMENT_RELEVANT_FLAG,
						qnaire_version.MEASUREMENT_HIDDEN_FLAG,
						qnaire_version.SERIES_RELEVANT_FLAG,
						qnaire_version.SERIES_HIDDEN_FLAG,
						qnaire_version.LINK_RELEVANT_FLAG,
						qnaire_version.LINK_HIDDEN_FLAG,
						qnaire_version.OPEN_POINTS_RELEVANT_FLAG,
						qnaire_version.OPEN_POINTS_HIDDEN_FLAG,
						qnaire_version.REMARK_RELEVANT_FLAG,
						qnaire_version.REMARKS_HIDDEN_FLAG,
						max_vers,
						0,
						'Migrated version for Qnaire Version '||qnaire_version.MAJOR_VERSION_NUM||'.'||qnaire_version.MINOR_VERSION_NUM || ' with general and available exisiting questions',
						'',
						qnaire_version.MEASURE_RELAVENT_FLAG,
						qnaire_version.MEASURE_HIDDEN_FLAG,
						qnaire_version.RESPONSIBLE_RELAVENT_FLAG,
						qnaire_version.RESPONSIBLE_HIDDEN_FLAG,
						qnaire_version.COMPLETION_DATE_RELAVENT_FLAG,
						qnaire_version.COMPLETION_DATE_HIDDEN_FLAG,
						qnaire_version.QNAIRE_VERS_ID
						)RETURNING QNAIRE_VERS_ID INTO v_temp_qnaire_vers_id;
				
				pk_log.info('Related Qnaire Vers ID '|| qnaire_version.QNAIRE_VERS_ID);
				pk_log.info('Inserted in T_QUESTIONNAIRE_VERSION '||v_temp_qnaire_vers_id);
				--clearing the general qnaire version id     
				v_temp_gen_qnaire_vers_id := null;
				--finding the general questionnaire version id used for the qnaire version id 
				find_general_qnaire_version(qnaire_version.qnaire_vers_id,v_general_ques_id,v_temp_gen_qnaire_vers_id);
				
				--For Adding General Question   
				FOR v_ques_gen_parent IN (select * from t_question where qnaire_vers_id = v_temp_gen_qnaire_vers_id and Heading_flag ='Y' and PARENT_Q_ID is null order by q_number) LOOP
					pk_log.info('Adding General Questions form the General Question Version '|| v_temp_gen_qnaire_vers_id);
					create_ques_for_new_version(v_temp_qnaire_vers_id,v_ques_gen_parent.q_id,v_ques_gen_parent.q_number);
					pk_log.info('Added General Questions to the Version ' || v_temp_qnaire_vers_id);
				END LOOP; 
				
				--For Adding Existing Question
				FOR v_ques_parent IN (select * from t_question where qnaire_vers_id = qnaire_version.qnaire_vers_id and Heading_flag ='Y' and PARENT_Q_ID is null order by q_number) LOOP
					pk_log.info('Adding Existing Questions form the Question Version '|| qnaire_version.qnaire_vers_id);
					select max(q_number) into v_temp_question_number from t_question where qnaire_vers_id = v_temp_qnaire_vers_id and Heading_flag ='Y' and PARENT_Q_ID is null ;
					v_temp_question_number := v_temp_question_number + 1;
					pk_log.info('Next Question Number'|| v_temp_question_number);
					create_ques_for_new_version(v_temp_qnaire_vers_id,v_ques_parent.q_id,v_temp_question_number);
					pk_log.info('Added Existing Questions to the Version ' || v_temp_qnaire_vers_id);
				END LOOP;
				
				--for general questions
				FOR v_question_gen_val IN (select * from t_question where qnaire_vers_id = v_temp_gen_qnaire_vers_id and Heading_flag ='Y' and PARENT_Q_ID is null order by q_number) LOOP
					pk_log.info('Starting update dependency question for general question');
					update_ques_dep_for_new_vers(v_temp_qnaire_vers_id,v_question_gen_val.q_id);
					pk_log.info('Completed update dependency question for general question');
				END LOOP;
				
				--for existing questions 
				FOR v_question_ext_val IN (select * from t_question where qnaire_vers_id = qnaire_version.qnaire_vers_id and Heading_flag ='Y' and PARENT_Q_ID is null order by q_number) LOOP
					pk_log.info('Starting update dependency question for exisiting question');
					update_ques_dep_for_new_vers(v_temp_qnaire_vers_id,v_question_ext_val.q_id);
					pk_log.info('Completed update dependency question for exisiting question');
				END LOOP;
			END IF;

			v_qnaire_def_ver_counter := v_qnaire_def_ver_counter + 1;
			pk_log.info('Completed Questionnaire Definition Versions Count '|| v_qnaire_def_ver_counter ||'/'||qnaire_vers_cur%ROWCOUNT);

		EXCEPTION
			WHEN OTHERS THEN
				PK_LOG.ERROR('Error in Creation Questionnaire Definition Versions : ',sqlcode,sqlerrm); 
				PK_LOG.END_JOB;
				RAISE_APPLICATION_ERROR(-20000, 'Error in Creation Questionnaire Definition Versions ');
		END;
    END LOOP;   
    close qnaire_vers_cur;
	
    pk_log.info('Completed Creation of All New Questionnaire Definition Versions with General and Existing questions');
    -- END for creating new questionnaire version using general and existing question
	PK_LOG.END_JOB;
	
EXCEPTION 
    when others then
		PK_LOG.ERROR('Error in Step 1 Questionnaire Data Migration Job',sqlcode,sqlerrm); 
		PK_LOG.END_JOB;
		RAISE_APPLICATION_ERROR(-20000, 'Error in Step 1 Questionnaire Data Migration Job');
end;
/

spool off
