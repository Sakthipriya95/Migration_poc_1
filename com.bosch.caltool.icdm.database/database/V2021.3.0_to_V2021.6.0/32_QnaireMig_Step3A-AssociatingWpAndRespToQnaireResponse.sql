spool c:\temp\32_QnaireMig_Step3A-AssociatingWpAndRespToQnaireResponse.log

-----------------------------------------------------------------
-- Script is to : 
--   a) associate qnaire response with a2l_wp_id and a2l_resp_id from review results
--   b) replicates qnaire response for each combination of A2L wp and A2L resp
--   c) create 'common' gen qnaire resp for wp-resp combination
--
-- IMPORTANT : before commiting, run the next validation script !!!
-----------------------------------------------------------------

set serveroutput on size unlimited

WHENEVER SQLERROR EXIT SQL.SQLCODE ROLLBACK;



--*************
-- DECLARE MAIN
--*************
declare

    cBATCH_COMMIT_SIZE constant number := 200;
    
    --step 3 variables 
    --to store result id
    v_result_id T_RVW_QNAIRE_RESULTS.result_id%type;
    --to qnaire resp id
    v_qnaire_resp_id T_RVW_QNAIRE_RESULTS.qnaire_resp_id%type;
    
    --a2l workpackage id
    v_a2l_wp_id T_RVW_WP_RESP.a2l_wp_id%type;
    --a2l responsibility id
    v_a2l_resp_id T_RVW_WP_RESP.a2l_resp_id%type;
    --pidc version id
    v_q_pidc_vers_id T_RVW_QNAIRE_RESPONSE.PIDC_VERS_ID%type;
    
    v_q_pidc_a2l_id T_RVW_RESULTS.PIDC_A2L_ID%type;
    
    v_q_variant_id t_rvw_variants.variant_id%type;
    
    v_variant_present number;
    
    v_gen_qnaire_vers_id number;
    
    v_general_ques_id number;
    
    --to check if any rows found in wp and resp
    any_wp_resp_found number;
    
    v_commiter_counter number;

    --cursor to hold result id and qnaire resp id for Questionnaire Resp Migration to WP and RESP combination
    CURSOR result_qnaire_cur IS 
        select result_id,qnaire_resp_id 
        from T_RVW_QNAIRE_RESULTS 
        where MIG_STATE = 'N'; 
        
    --*************************************************
    --procedure to fetch/create default wp and resp ids
    --*************************************************
    procedure get_Default_wp_and_resp
        (p_pidc_vers_id IN NUMBER, po_a2l_wp_id OUT NUMBER, po_a2l_resp_id OUT NUMBER)
    IS
    
        v_project_id t_pidc_version.project_id%type;
        
        v_pRespType t_a2l_responsibility.RESP_TYPE%type :='R';
        
        cDefaultWpName constant varchar2(12) := '_DEFAULT_WP_';
       
        CURSOR a2l_resp_cur (proj_id in number) IS 
            select a2l_resp_id from t_a2l_responsibility
            where project_id = proj_id
                and resp_type = v_pRespType
                -- if pRespName is null, alias name will be decoded out of RESP_TYPE
                and alias_name = nvl('RB', decode(v_pRespType, 'R', 'RB', 'C', 'CUST', 'O', 'OTHER', null));
             
        CURSOR a2l_wp_cur(pidc_ver_id in number) IS 
            SELECT A2L_WP_ID from t_a2l_work_packages 
            where pidc_vers_id = pidc_ver_id
                and WP_NAME = cDefaultWpName  ;
               
        t_a2l_resp_cur a2l_resp_cur%rowtype;
        
        t_a2l_wp_cur a2l_wp_cur%rowtype;
    
    BEGIN
        select project_id into v_project_id 
            from t_pidc_version 
            where PIDC_VERS_ID = p_pidc_vers_id;

        open a2l_resp_cur(v_project_id);
        fetch a2l_resp_cur into  t_a2l_resp_cur;
        if (a2l_resp_cur%NOTFOUND) 
        then
            insert into t_a2l_responsibility
                (PROJECT_ID, ALIAS_NAME, L_DEPARTMENT, RESP_TYPE, CREATED_USER)
            values
            (  
                  v_project_id
                , nvl('RB', decode(v_pRespType, 'R', 'RB', 'C', 'CUST', 'O', 'OTHER', null))
                , 'RB'
                , v_pRespType
                , USER
            )
            RETURNING a2l_resp_id into  po_a2l_resp_id;
            
            pk_log.info('A2L Resp Data INSERT DONE' || po_a2l_resp_id);
        
        ELSE
            po_a2l_resp_id := t_a2l_resp_cur.a2l_resp_id;
        end if;
        close a2l_resp_cur;

        open a2l_wp_cur(p_pidc_vers_id);
        fetch a2l_wp_cur into  t_a2l_wp_cur;
        if (a2l_wp_cur%NOTFOUND) then
            INSERT INTO T_A2L_WORK_PACKAGES(WP_NAME, WP_DESC, PIDC_VERS_ID,CREATED_USER) 
            VALUES
            (
            cDefaultWpName, 'Default Workpackage created by iCDM', p_pidc_vers_id, USER
            ) 
            RETURNING a2l_wp_id into  po_a2l_wp_id;
            
            pk_log.info('A2L WP Data INSERT DONE ' || po_a2l_wp_id);
        ELSE
            po_a2l_wp_id := t_a2l_wp_cur.A2L_WP_ID;
        END IF;
        
        close a2l_wp_cur;
        
    END;

    --**********************************************************************
    -- procedure to create qnaire resp versions and its associated tables
    --**********************************************************************
    PROCEDURE create_qnaire_resp_version(
        p_qnaire_resp_id IN NUMBER, p_new_qnaire_resp_id IN NUMBER) 
    IS
    
        v_temp_qnaire_resp_vers_id T_RVW_QNAIRE_RESP_VERSIONS.QNAIRE_RESP_VERS_ID%type;
        v_temp_qnaire_answer_id T_RVW_QNAIRE_ANSWER.RVW_ANSWER_ID%type;
        v_rvw_qnaire_resp_version T_RVW_QNAIRE_RESP_VERSIONS%ROWTYPE;

        v_count number;
    
    BEGIN 
        --storing questionnaire response versions rows value
        select count(1) into v_count 
        from T_RVW_QNAIRE_RESP_VERSIONS 
        where qnaire_resp_id = p_qnaire_resp_id and REV_NUM = 0;            

        select * into v_rvw_qnaire_resp_version 
        from T_RVW_QNAIRE_RESP_VERSIONS 
        where qnaire_resp_id = p_qnaire_resp_id and REV_NUM = 0;
        
        --Insert into T_RVW_QNAIRE_RESP_VERSIONS
        INSERT INTO T_RVW_QNAIRE_RESP_VERSIONS 
            (QNAIRE_RESP_ID,NAME,DESCRIPTION,REV_NUM,QNAIRE_VERS_ID,QNAIRE_VERS_STATUS)
        VALUES
        (
            p_new_qnaire_resp_id,
            v_rvw_qnaire_resp_version.NAME,
            v_rvw_qnaire_resp_version.DESCRIPTION,
            v_rvw_qnaire_resp_version.REV_NUM,
            v_rvw_qnaire_resp_version.QNAIRE_VERS_ID,
            v_rvw_qnaire_resp_version.QNAIRE_VERS_STATUS
        )
        RETURNING QNAIRE_RESP_VERS_ID INTO v_temp_qnaire_resp_vers_id ;
                 
        --to iterate all the rvw answers to create a copy of review answer
        FOR f_rvw_answer IN 
            (select * from T_RVW_QNAIRE_ANSWER where QNAIRE_RESP_VERS_ID = v_rvw_qnaire_resp_version.QNAIRE_RESP_VERS_ID) 
        LOOP
                            
            --Insert into T_RVW_QNAIRE_ANSWER
            INSERT INTO T_RVW_QNAIRE_ANSWER 
                (Q_ID,RESULT,MEASUREMENT,SERIES,REMARK,Q_RESULT_OPT_ID,QNAIRE_RESP_ID,QNAIRE_RESP_VERS_ID)
            VALUES
            (
                f_rvw_answer.Q_ID,
                f_rvw_answer.RESULT,
                f_rvw_answer.MEASUREMENT,
                f_rvw_answer.SERIES,
                f_rvw_answer.REMARK,
                f_rvw_answer.Q_RESULT_OPT_ID,
                p_new_qnaire_resp_id,
                v_temp_qnaire_resp_vers_id
            )
            RETURNING RVW_ANSWER_ID into v_temp_qnaire_answer_id;
            
            pk_log.info('New RVW_QNAIRE_ANSWER created. ID : ' || v_temp_qnaire_answer_id);
            
            --Copy Answer OPLs
            INSERT INTO T_RVW_QNAIRE_ANSWER_OPL 
                    (OPEN_POINTS, MEASURE, RESPONSIBLE, COMPLETION_DATE, RESULT, RVW_ANSWER_ID)
                select
                    OPEN_POINTS, MEASURE, RESPONSIBLE, COMPLETION_DATE, RESULT, v_temp_qnaire_answer_id
                from T_RVW_QNAIRE_ANSWER_OPL
                where RVW_ANSWER_ID = f_rvw_answer.rvw_answer_id;

            
            --Copy Answer Links
            INSERT INTO T_LINKS
                    (NODE_ID, NODE_TYPE, LINK_URL, DESC_ENG, DESC_GER)
                select 
                    v_temp_qnaire_answer_id, NODE_TYPE, LINK_URL, DESC_ENG, DESC_GER
                from T_LINKS
                where NODE_ID = f_rvw_answer.rvw_answer_id;

            
        END LOOP;
        
        pk_log.info('Completed Creating of Working Set Version');
        
    END;
    
    --*****************************************************************************
    --procedure to create 'special' qnaire resp versions and its associatted tables
    --*****************************************************************************
    PROCEDURE create_spec_qnaire_resp_vers
        (p_qnaire_resp_id IN NUMBER, p_new_qnaire_resp_id IN NUMBER) 
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
        pk_log.info('Started Creation of Special Questionnaire Response Versions ');
        pk_log.info('Qnaire Resp ID '||p_qnaire_resp_id);
        
        --storing questionnaire response versions rows value
        select * into v_rvw_qnaire_resp_version 
            from T_RVW_QNAIRE_RESP_VERSIONS 
            where qnaire_resp_id = p_qnaire_resp_id and REV_NUM = 0;
        pk_log.info('RELATED_QNAIRE_VERS_ID '||v_rvw_qnaire_resp_version.QNAIRE_VERS_ID);
        
        select count(1) into v_count from T_QUESTIONNAIRE_VERSION WHERE RELATED_QNAIRE_VERS_ID = v_rvw_qnaire_resp_version.QNAIRE_VERS_ID;          
        pk_log.info('RELATED_QNAIRE_VERS_ID count : '|| v_count );
        
        select * into v_question_version from T_QUESTIONNAIRE_VERSION WHERE RELATED_QNAIRE_VERS_ID = v_rvw_qnaire_resp_version.QNAIRE_VERS_ID;
        
        --add entry in T_RVW_QNAIRE_RESP_VERSIONS
        
        select max(REV_NUM) into max_rev_num from T_RVW_QNAIRE_RESP_VERSIONS where qnaire_resp_id = p_qnaire_resp_id;
        
        INSERT INTO T_RVW_QNAIRE_RESP_VERSIONS 
            (QNAIRE_RESP_ID,NAME,DESCRIPTION,REV_NUM,QNAIRE_VERS_ID,QNAIRE_VERS_STATUS,REVIEWED_DATE,REVIEWED_USER)
        VALUES
        (
                p_new_qnaire_resp_id,
                'Migrated version with both general and exisiting question',
                'Migrated version with both general and exisiting question',
                max_rev_num+1,
                v_question_version.QNAIRE_VERS_ID,
                v_rvw_qnaire_resp_version.QNAIRE_VERS_STATUS,
                sys_extract_utc(systimestamp),
                USER
        ) RETURNING QNAIRE_RESP_VERS_ID INTO v_temp_qnaire_resp_vers_id ;
        
        pk_log.info('Started Creation of Special Questionnaire Response Versions');     

        --to iterate all the rvw answers to create a copy of review answer
        FOR f_rvw_answer IN 
            (select * from T_RVW_QNAIRE_ANSWER where QNAIRE_RESP_VERS_ID = v_rvw_qnaire_resp_version.QNAIRE_RESP_VERS_ID) 
        LOOP
            
            select count(1) into v_count 
                from T_QUESTION 
                where RELATED_Q_ID = f_rvw_answer.Q_ID and qnaire_vers_id = v_question_version.QNAIRE_VERS_ID;
            
            pk_log.info('Match Question count  '||v_count);
            
            IF v_count = 0 THEN
                pk_log.info('check 1 for gen question ');
                
                select * into v_gen_question from T_QUESTION where Q_ID = f_rvw_answer.Q_ID;
                pk_log.info('check 2 for gen question ');
                
                select * into v_question from T_QUESTION where q_number = v_gen_question.q_number and q_name_eng = v_gen_question.q_name_eng and qnaire_vers_id = v_question_version.QNAIRE_VERS_ID;
                pk_log.info('Found the question for existing version ' || v_question.RELATED_Q_ID);
            ELSE
                pk_log.info('Rvw Answer ID '|| f_rvw_answer.RVW_ANSWER_ID || ' QNAIRE_RESP VERSION ID ' || v_question_version.QNAIRE_VERS_ID);
                pk_log.info('Answers Question ID  '|| f_rvw_answer.Q_ID || ' Count ' || v_count);
                
                --fetch the latest question id based on related q id     
                select * into v_question 
                from T_QUESTION 
                where RELATED_Q_ID = f_rvw_answer.Q_ID and qnaire_vers_id = v_question_version.QNAIRE_VERS_ID;
                pk_log.info('check 3 for gen question ');
                
            END IF;
            
            v_question_id := v_question.Q_ID;
            
            pk_log.info('Result Option value  '|| f_rvw_answer.Q_RESULT_OPT_ID || ' Question Id '|| v_question_id);
            
            if f_rvw_answer.Q_RESULT_OPT_ID is not null 
            then
            
                --fetch the latest question result option id based on related q id 
                pk_log.info('check line no 337 for rvw answer ');
                
                select count(1) into v_result_opt_count 
                  from T_QUESTION_RESULT_OPTIONS 
                  where RELATED_Q_RESULT_OPT_ID = f_rvw_answer.Q_RESULT_OPT_ID
                      and q_id = v_question_id;
                
                pk_log.info('Related Result Option found '|| v_result_opt_count);
                
                IF v_result_opt_count = 0 
                THEN
                    select * into v_temp_question_res_opt 
                        from T_QUESTION_RESULT_OPTIONS 
                        where Q_RESULT_OPT_ID = f_rvw_answer.Q_RESULT_OPT_ID;
                    
                    pk_log.info('check line no 347 for rvw answer ');   
                    pk_log.info('Q_RESULT_TYPE '|| v_temp_question_res_opt.Q_RESULT_TYPE);
                    pk_log.info('Q_RESULT_NAME '|| v_temp_question_res_opt.Q_RESULT_NAME);
                    pk_log.info('Q_ID '|| v_question_id);
                    pk_log.info('check line no 353 for rvw answer ');                       
                    select * into v_question_res_opt 
                        from T_QUESTION_RESULT_OPTIONS 
                        where q_id = v_question_id 
                            and Q_RESULT_TYPE = v_temp_question_res_opt.Q_RESULT_TYPE 
                            and Q_RESULT_NAME = v_temp_question_res_opt.Q_RESULT_NAME;
                    
                
                ELSE
                    pk_log.info('check line no 356 for rvw answer ');
                    select * into v_question_res_opt 
                        from T_QUESTION_RESULT_OPTIONS 
                        where RELATED_Q_RESULT_OPT_ID = f_rvw_answer.Q_RESULT_OPT_ID
                            and q_id = v_question_id;
                    pk_log.info('check line no 360 for rvw answer ');
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
                    p_new_qnaire_resp_id,
                    v_temp_qnaire_resp_vers_id
                ) RETURNING RVW_ANSWER_ID into v_temp_qnaire_answer_id;
            
            pk_log.info('New RVW_QNAIRE_ANSWER created for ''special'' resp version. ID : ' || v_temp_qnaire_answer_id);
            
            --Copy Answer OPLs
            INSERT INTO T_RVW_QNAIRE_ANSWER_OPL 
                    (OPEN_POINTS, MEASURE, RESPONSIBLE, COMPLETION_DATE, RESULT, RVW_ANSWER_ID)
                select
                    OPEN_POINTS, MEASURE, RESPONSIBLE, COMPLETION_DATE, RESULT, v_temp_qnaire_answer_id
                from T_RVW_QNAIRE_ANSWER_OPL
                where RVW_ANSWER_ID = f_rvw_answer.rvw_answer_id;


            --Copy Answer Links
            INSERT INTO T_LINKS
                    (NODE_ID, NODE_TYPE, LINK_URL, DESC_ENG, DESC_GER)
                select 
                    v_temp_qnaire_answer_id, NODE_TYPE, LINK_URL, DESC_ENG, DESC_GER
                from T_LINKS
                where NODE_ID = f_rvw_answer.rvw_answer_id;
           
        END LOOP; 
        
        --Adding Review Answers for the question that not available in the source questionniare response
        --later the non relevent review answers will be removed using a java logic
        INSERT INTO T_RVW_QNAIRE_ANSWER 
                (Q_ID, QNAIRE_RESP_ID, QNAIRE_RESP_VERS_ID)
            select 
                Q_ID, p_new_qnaire_resp_id, v_temp_qnaire_resp_vers_id
            from T_QUESTION
            where QNAIRE_VERS_ID = v_question_version.QNAIRE_VERS_ID
                and Q_ID not in 
                    (
                        select q_id 
                        from T_RVW_QNAIRE_ANSWER 
                        where QNAIRE_RESP_VERS_ID = v_temp_qnaire_resp_vers_id
                    );
        -------------------------------------------------------
        
        
        pk_log.info('Completed Creation of Special Questionnaire Response Versions');
        
    END;
    
    --********************************************************************************************************************
    --procedure to Update wp and resp in questionnaire response / Create copy of questionnaire response and its sub tables
    --********************************************************************************************************************
    procedure qnaire_migration
        (p_pidc_vers_id IN NUMBER, p_qnaire_resp_id IN NUMBER, p_variant_id IN NUMBER, p_a2l_wp_id IN NUMBER, p_a2l_resp_id IN NUMBER) 
    IS
        --variables to hold row contents
        v_rvw_qnaire_response T_RVW_QNAIRE_RESPONSE%ROWTYPE;
        v_rvw_qnaire_resp_version T_RVW_QNAIRE_RESP_VERSIONS%ROWTYPE;
        v_rvw_qnaire_resp_variant T_RVW_QNAIRE_RESP_VARIANTS%ROWTYPE;
        --temp variables to hold ids
        v_temp_qnaire_resp_id T_RVW_QNAIRE_RESPONSE.qnaire_resp_id%type;
        
        v_inner_code  NUMBER;
    
        v_inner_errm  VARCHAR2(64);
        
        l_b_var2  BOOLEAN:=false;
        
        v_is_qnaire_resp_available number;
        
    BEGIN
    
        --storing questionnaire response rows value
        select * into v_rvw_qnaire_response 
            from T_RVW_QNAIRE_RESPONSE 
            where qnaire_resp_id = p_qnaire_resp_id;
    
        if p_variant_id is null 
        then
            select count(1) into v_is_qnaire_resp_available 
            from t_rvw_qnaire_response 
            where QNAIRE_VERS_ID = v_rvw_qnaire_response.QNAIRE_VERS_ID 
                and pidc_vers_id = p_pidc_vers_id
                and variant_id is null
                and a2l_wp_id = p_a2l_wp_id 
                and a2l_resp_id = p_a2l_resp_id ;
        ELSE 
            select count(1) into v_is_qnaire_resp_available 
            from t_rvw_qnaire_response 
            where QNAIRE_VERS_ID = v_rvw_qnaire_response.QNAIRE_VERS_ID 
                and pidc_vers_id = p_pidc_vers_id
                and variant_id = p_variant_id 
                and a2l_wp_id = p_a2l_wp_id 
                and a2l_resp_id = p_a2l_resp_id ;
        END IF ;
        
        -- Avoid copying to same wp+resp again for given variant/pidc version
        if v_is_qnaire_resp_available = 0 
        then
            pk_log.info('Question Migration Started for  A2L_WP_ID : '|| p_a2l_wp_id || ' A2L_RESP_ID : ' || p_a2l_resp_id || ' Variant_ID : '|| p_variant_id );
            pk_log.info('Qnaire Resp Id '|| p_qnaire_resp_id);       
            
            IF (((v_rvw_qnaire_response.VARIANT_ID is null and p_variant_id is null)or v_rvw_qnaire_response.VARIANT_ID = p_variant_id) 
              AND v_rvw_qnaire_response.a2l_wp_id is null and v_rvw_qnaire_response.a2l_resp_id is null) 
            THEN
            
                pk_log.info('Updating Existing Questionnaire Response with WP and RESP ID');
                
                --Update the T_RVW_QNAIRE_RESPONSE with a2l resp id and a2l wp id
                pk_log.info(p_a2l_wp_id || '   ' || p_a2l_resp_id);
                
                update T_RVW_QNAIRE_RESPONSE 
                    set A2L_WP_ID = p_a2l_wp_id,A2L_RESP_ID = p_a2l_resp_id  
                    where QNAIRE_RESP_ID = p_qnaire_resp_id;
                
                pk_log.info('Updating Completed');
                
                --for adding special questionnaire version
                create_spec_qnaire_resp_vers(p_qnaire_resp_id,p_qnaire_resp_id);

            ELSIF 
              (((v_rvw_qnaire_response.VARIANT_ID is null and p_variant_id is null)or v_rvw_qnaire_response.VARIANT_ID = p_variant_id)
               AND v_rvw_qnaire_response.a2l_wp_id != p_a2l_wp_id AND v_rvw_qnaire_response.a2l_resp_id != p_a2l_resp_id) then
                
                pk_log.info('Creating a new Copy of Existing Questionnaire Response with new WP and RESP ID for PIDC Version Id '||v_rvw_qnaire_response.PIDC_VERS_ID);
                --Preparing for copy of questionnaire response and its associated table contents
                
                --Insert into T_RVW_QNAIRE_RESPONSE    
                INSERT INTO T_RVW_QNAIRE_RESPONSE 
                    (PIDC_VERS_ID,VARIANT_ID,QNAIRE_VERS_ID,REVIEWED_FLAG,REVIEWED_USER,REVIEWED_DATE,DELETED_FLAG,A2L_WP_ID,A2L_RESP_ID,COPIED_FROM_QNAIRE_RESP_ID) 
                VALUES 
                (       v_rvw_qnaire_response.PIDC_VERS_ID,
                        v_rvw_qnaire_response.VARIANT_ID,
                        v_rvw_qnaire_response.QNAIRE_VERS_ID,
                        v_rvw_qnaire_response.REVIEWED_FLAG,
                        v_rvw_qnaire_response.REVIEWED_USER,
                        v_rvw_qnaire_response.REVIEWED_DATE,
                        v_rvw_qnaire_response.DELETED_FLAG,
                        p_a2l_wp_id,
                        p_a2l_resp_id,
                        p_qnaire_resp_id
                ) RETURNING qnaire_resp_id INTO v_temp_qnaire_resp_id;
                
                pk_log.info('Inserted T_RVW_QNAIRE_RESPONSE');

                --storing questionnaire response variants rows value
                select * into v_rvw_qnaire_resp_variant 
                  from T_RVW_QNAIRE_RESP_VARIANTS 
                  where qnaire_resp_id = p_qnaire_resp_id;
                
                --Insert into T_RVW_QNAIRE_RESP_VARIANTS
                
                INSERT INTO T_RVW_QNAIRE_RESP_VARIANTS
                    (PIDC_VERS_ID,VARIANT_ID,QNAIRE_RESP_ID)
                VALUES
                    (
                        v_rvw_qnaire_resp_variant.PIDC_VERS_ID,
                        v_rvw_qnaire_resp_variant.VARIANT_ID,
                        v_temp_qnaire_resp_id
                    );
                
                pk_log.info('Inserted T_RVW_QNAIRE_RESP_VARIANTS');
                
                create_qnaire_resp_version(p_qnaire_resp_id,v_temp_qnaire_resp_id);
                
                pk_log.info('Completed creating a new Copy of Existing Questionnaire Response with new WP and RESP ID');
                
                --for addning special questionnaire version
                create_spec_qnaire_resp_vers(p_qnaire_resp_id,v_temp_qnaire_resp_id);
            END IF;
        END IF;
    EXCEPTION
        WHEN OTHERS THEN
            PK_LOG.ERROR('Error in Questionnaire Migration : ',sqlcode,sqlerrm); 
            PK_LOG.END_JOB;
            RAISE_APPLICATION_ERROR(-20000, 'Error in Questionnaire Migration '); 
    END;
    
    --**************************************************************************
    -- create response for general questionnaire for the VAR-WP-RESP combination
    --**************************************************************************
    PROCEDURE create_general_question_resp
        (p_gen_qnaire_vers_id IN NUMBER, p_pidc_vers_id IN NUMBER, p_variant_id IN NUMBER, p_a2l_wp_id IN NUMBER, p_a2l_resp_id IN NUMBER) 
    IS 

        v_temp_qnaire_resp_id number;
        v_temp_qnaire_resp_vers_id number;
        v_is_gen_qnaire_available number := 0;
        
        v_inner_code  NUMBER;
    
        v_inner_errm  VARCHAR2(64);

    BEGIN 
    
        if p_variant_id is null 
        then
            select count(1) into v_is_gen_qnaire_available 
            from t_rvw_qnaire_response 
            where QNAIRE_VERS_ID = p_gen_qnaire_vers_id 
                and pidc_vers_id = p_pidc_vers_id
                and variant_id is null
                and a2l_wp_id = p_a2l_wp_id 
                and a2l_resp_id = p_a2l_resp_id ;
        ELSE 
            select count(1) into v_is_gen_qnaire_available 
            from t_rvw_qnaire_response 
            where QNAIRE_VERS_ID = p_gen_qnaire_vers_id 
                and pidc_vers_id = p_pidc_vers_id
                and variant_id = p_variant_id 
                and a2l_wp_id = p_a2l_wp_id 
                and a2l_resp_id = p_a2l_resp_id ;
        END IF ;
        
        pk_log.info ('general question count '|| v_is_gen_qnaire_available);
        
        if v_is_gen_qnaire_available = 0 
        then  
            pk_log.info('Started Creating General Question for A2L WP ID '|| p_a2l_wp_id || 'and A2L RESP ID ' || p_a2l_resp_id);
            pk_log.info('pidc vers id '|| p_pidc_vers_id || ' variant id  ' || p_variant_id || 'gen question ver id '||p_gen_qnaire_vers_id);
            
            --Insert into T_RVW_QNAIRE_RESPONSE    
            INSERT INTO T_RVW_QNAIRE_RESPONSE 
                (PIDC_VERS_ID,VARIANT_ID,QNAIRE_VERS_ID,REVIEWED_FLAG,DELETED_FLAG,A2L_WP_ID,A2L_RESP_ID) 
            VALUES 
                (   p_pidc_vers_id,
                    p_variant_id,
                    p_gen_qnaire_vers_id,
                    'N',
                    'N',
                    p_a2l_wp_id,
                    p_a2l_resp_id
                ) RETURNING qnaire_resp_id INTO v_temp_qnaire_resp_id;
            
            --Insert into T_RVW_QNAIRE_RESP_VERSIONS         
            INSERT INTO T_RVW_QNAIRE_RESP_VERSIONS 
                (QNAIRE_RESP_ID,NAME,DESCRIPTION,REV_NUM,QNAIRE_VERS_ID,QNAIRE_VERS_STATUS)
            VALUES
                (
                    v_temp_qnaire_resp_id,
                    'Working Set',
                    'Created during migration',
                    '0',
                    p_gen_qnaire_vers_id,
                    '-'
                ) RETURNING QNAIRE_RESP_VERS_ID INTO v_temp_qnaire_resp_vers_id ;
                
            --Insert into T_RVW_QNAIRE_RESP_VARIANTS
            INSERT INTO T_RVW_QNAIRE_RESP_VARIANTS
                (PIDC_VERS_ID,VARIANT_ID,QNAIRE_RESP_ID)
            VALUES
                (
                    p_pidc_vers_id,
                    p_variant_id,
                    v_temp_qnaire_resp_id
                );
            
            pk_log.info('Completed creating of General Question for A2L WP ID '|| p_a2l_wp_id || 'and A2L RESP ID ' || p_a2l_resp_id);
            
        END IF;
        
    EXCEPTION
        WHEN OTHERS THEN
            PK_LOG.ERROR('Error in Creation of General Questionnaire Response : ',sqlcode,sqlerrm); 
            PK_LOG.END_JOB;
            RAISE_APPLICATION_ERROR(-20000, 'Error in Creation of General Questionnaire Response '); 
    END;
    --END for Questionnaire Resposne Migration

--*************************************************
-- BEGIN MAIN
--*************************************************
BEGIN 
    PK_LOG.START_NEW_JOB('Step3_Questionnaire_Data_Migration_Job');
    
    --fetch general questionnaire version id
    select param_value into v_general_ques_id from tabv_common_params where param_id = 'GENERAL_QNAIRE_ID';
    
    select qnaire_vers_id into v_gen_qnaire_vers_id 
      from t_questionnaire_version 
      where qnaire_id = v_general_ques_id and active_flag = 'Y';
    pk_log.info('Active General Questionnaire Version ID : '||v_gen_qnaire_vers_id);
    --end of fetch general question version id
  
    pk_log.info('Started of Questionnaire Resp Migration to WP and RESP combination');
    
    v_commiter_counter := 0;
    
    --open the cursor
    open result_qnaire_cur;
    LOOP
        --fetching from the cursor
        FETCH result_qnaire_cur into v_result_id,v_qnaire_resp_id;
        EXIT WHEN result_qnaire_cur%notfound;
        
        pk_log.info('Started Questionnaire Migration for -> QNAIRE_RESP_ID : '|| v_qnaire_resp_id || ', RESULT_ID : '|| v_result_id);
        
        --to check whether the wp resp is available for the result id
        select count(1) into any_wp_resp_found
            from t_rvw_wp_resp 
            where RESULT_ID = v_result_id;
                
        select PIDC_A2L_ID into v_q_pidc_a2l_id
            from T_RVW_RESULTS 
            where RESULT_ID = v_result_id;
                
        --to fetch the pidc version for the questionniare response
        select pidc_vers_id into v_q_pidc_vers_id
            from T_RVW_QNAIRE_RESPONSE
            where QNAIRE_RESP_ID = v_qnaire_resp_id;
        
        v_variant_present := 0;
        v_q_variant_id := null;

        select count(1) into v_variant_present from t_rvw_variants where result_id = v_result_id;
        
        if v_variant_present > 0 
        then
            SELECT variant_id into v_q_variant_id 
            FROM 
                (select variant_id 
                    from t_rvw_variants  
                    where result_id = v_result_id 
                    ORDER BY created_date ASC
                ) 
            WHERE ROWNUM = 1;
        end if;
        
        pk_log.info('Qnaire Resp Id : '||v_qnaire_resp_id || ', Result Id :'|| v_result_id || ', Variant ID :' || v_q_variant_id);
        
        IF any_wp_resp_found > 0 
        THEN 
            --iterating the identified wp and resp and creating questionnaire response for each combination
            FOR wpResp in 
                (
                    select distinct a2l_wp_id, a2l_resp_id 
                    from t_temp_qnaireresp_wpresp
                    where qnaire_resp_id = v_qnaire_resp_id
                ) 
            LOOP
                pk_log.info('Replicating qnaire response for PIDC version :' || v_q_pidc_vers_id || ', VAR :' || v_q_variant_id || ', WP :' || wpResp.a2l_wp_id || ', RESP :' || wpResp.a2l_resp_id);
                --create general question 
                create_general_question_resp(v_gen_qnaire_vers_id, v_q_pidc_vers_id, v_q_variant_id, wpResp.a2l_wp_id, wpResp.a2l_resp_id);
                --procedure call to questionnaire migration
                qnaire_migration(v_q_pidc_vers_id, v_qnaire_resp_id, v_q_variant_id, wpResp.a2l_wp_id, wpResp.a2l_resp_id);
            END LOOP;
        ELSE
            -- to create default workpackage defenition if there is no do default wp def not available for the pidc a2l id
            -- required for old review results, that do not have DEFAULT WP or RESP
            v_a2l_wp_id := null;
            v_a2l_resp_id := null;

            get_Default_wp_and_resp(v_q_pidc_vers_id, v_a2l_wp_id, v_a2l_resp_id);
            pk_log.info('Default A2L WP ID : '|| v_a2l_wp_id || ', Default A2L RESP ID : ' || v_a2l_resp_id);  
            
            IF(v_a2l_resp_id is not null AND v_a2l_wp_id is not null) THEN
                pk_log.info('Replicating qnaire response (after creating defaults) for PIDC version :' || v_q_pidc_vers_id || ', VAR :' || v_q_variant_id || ', WP :' || v_a2l_wp_id || ', RESP :' || v_a2l_resp_id);
                --create general question 
                create_general_question_resp(v_gen_qnaire_vers_id, v_q_pidc_vers_id, v_q_variant_id, v_a2l_wp_id, v_a2l_resp_id);
                --procedure call to questionnaire migration
                qnaire_migration(v_q_pidc_vers_id, v_qnaire_resp_id, v_q_variant_id, v_a2l_wp_id, v_a2l_resp_id);
            END IF;
        END IF;
        
        pk_log.info('Update the flag of current (result+resp) combination to completed');

        update T_RVW_QNAIRE_RESULTS set mig_state = 'C' where result_id = v_result_id and qnaire_resp_id = v_qnaire_resp_id;
        
        pk_log.info('Completed Questionnaire Migration for QNAIRE_RESP_ID : '|| v_qnaire_resp_id || ' and RESULT_ID : ' || v_result_id);
        
        v_commiter_counter := v_commiter_counter + 1;
        
        IF v_commiter_counter >= cBATCH_COMMIT_SIZE
        THEN
            pk_log.info('batch commit reached');
            commit;
            v_commiter_counter := 0;
        END IF;
        
    END LOOP;
    close result_qnaire_cur;

    -- final commit
    pk_log.info('final commit');
    commit;
    
    pk_log.info('End of Questionnaire Resp Replication to WP and RESP combination');

    PK_LOG.END_JOB;

EXCEPTION 
    when others then
        PK_LOG.ERROR('Error in step 3 qustionnaire data migration job',sqlcode,sqlerrm); 
        PK_LOG.END_JOB;
        RAISE_APPLICATION_ERROR(-20000, 'Error in step 3 qustionnaire data migration job'); 
end;
/

spool off