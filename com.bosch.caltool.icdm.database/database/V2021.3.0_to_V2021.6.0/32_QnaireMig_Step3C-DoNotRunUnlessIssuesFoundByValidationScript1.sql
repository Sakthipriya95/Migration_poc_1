spool c:\temp\32_QnaireMig_Step3C-DoNotRunUnlessIssuesFoundByValidationScript1.log


----------------------------------------------
--Fixing Script for Step 3
----------------------------------------------
----------------------------------------------
--Script to create general questions for questionnaire versions 
--Note : This script has to be run only if there are any pidc versions that doent have general questions for a specific a2l wp and resp
----------------------------------------------

WHENEVER SQLERROR EXIT SQL.SQLCODE ROLLBACK;

declare 

    v_genereal_question_found number;

    v_pidc_vers_id number;

    v_variant_id number;

    v_general_ques_id number;

    v_act_gen_qnaire_vers_id number;
	
    PROCEDURE create_general_question
        (v_gen_qnaire_vers_id IN NUMBER,t_pidc_vers_id IN NUMBER,t_variant_id IN NUMBER,t_a2l_wp_id IN NUMBER ,t_a2l_resp_id IN NUMBER) 
    IS 

        v_temp_qnaire_resp_id number;
        v_temp_qnaire_resp_vers_id number;
        v_is_gen_qnaire_available number := 0;
        
        v_inner_code  NUMBER;
    
        v_inner_errm  VARCHAR2(64);

    BEGIN 
        if t_variant_id is null 
        then
            select count(1) into v_is_gen_qnaire_available 
            from t_rvw_qnaire_response 
            where QNAIRE_VERS_ID = v_gen_qnaire_vers_id 
                and pidc_vers_id = t_pidc_vers_id
                and (variant_id is null)
                and a2l_wp_id = t_a2l_wp_id 
                and a2l_resp_id = t_a2l_resp_id ;
        ELSE 
            select count(1) into v_is_gen_qnaire_available 
            from t_rvw_qnaire_response 
            where QNAIRE_VERS_ID = v_gen_qnaire_vers_id 
                and pidc_vers_id = t_pidc_vers_id
                and (variant_id = t_variant_id )
                and a2l_wp_id = t_a2l_wp_id 
                and a2l_resp_id = t_a2l_resp_id ;
		END IF ;
        
        PK_LOG.info ('general question count '|| v_is_gen_qnaire_available);
        
        if v_is_gen_qnaire_available = 0 
        then  
            PK_LOG.info('Started Creating General Question for A2L WP ID '|| t_a2l_wp_id || 'and A2L RESP ID ' || t_a2l_resp_id);
            --Insert into T_RVW_QNAIRE_RESPONSE    
            INSERT INTO T_RVW_QNAIRE_RESPONSE 
                (PIDC_VERS_ID,VARIANT_ID,QNAIRE_VERS_ID,REVIEWED_FLAG,DELETED_FLAG,A2L_WP_ID,A2L_RESP_ID) 
            VALUES 
            (   
                t_pidc_vers_id,
                t_variant_id,
                v_gen_qnaire_vers_id,
                'N',
                'N',
                t_a2l_wp_id,
                t_a2l_resp_id
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
                v_gen_qnaire_vers_id,
                '-'
            ) RETURNING QNAIRE_RESP_VERS_ID INTO v_temp_qnaire_resp_vers_id ;
                
            --Insert into T_RVW_QNAIRE_RESP_VARIANTS
            INSERT INTO T_RVW_QNAIRE_RESP_VARIANTS
                (PIDC_VERS_ID,VARIANT_ID,QNAIRE_RESP_ID)
            VALUES
            (
                t_pidc_vers_id,
                t_variant_id,
                v_temp_qnaire_resp_id
            );
			
            PK_LOG.info('Completed creating of General Question for A2L WP ID '|| t_a2l_wp_id || 'and A2L RESP ID ' || t_a2l_resp_id);
            
        END IF;
    EXCEPTION
        WHEN OTHERS THEN
            PK_LOG.ERROR('Error in Creation of General Questionnaire Response : ',sqlcode,sqlerrm); 
            PK_LOG.END_JOB;
            RAISE_APPLICATION_ERROR(-20000, 'Error in Creation of General Questionnaire Response'); 
    END;


begin 

    PK_LOG.START_NEW_JOB('Step3C_Fixing_script_for_general_question');
    
    --getting general questionnaire id
    select param_value into v_general_ques_id from tabv_common_params where param_id = 'GENERAL_QNAIRE_ID';
    
    --geeting Active general questionnaire version id
    select qnaire_vers_id into v_act_gen_qnaire_vers_id from t_questionnaire_version where qnaire_id = v_general_ques_id and active_flag = 'Y';

    --input pidc version id
    FOR v_qnaire_resp in (select * from t_rvw_qnaire_response where pidc_vers_id = <pidc_vers_id>) 
    LOOP
            
        if v_qnaire_resp.variant_id is null 
        then 
            --if variant id is null
            select count(1) into v_genereal_question_found
            from t_questionnaire_version 
            where  qnaire_id = v_general_ques_id 
                and qnaire_vers_id in 
                  ( 
                    select QNAIRE_VERS_ID 
                    from t_rvw_qnaire_response 
                    where pidc_vers_id = v_qnaire_resp.pidc_vers_id 
                        and variant_id is null
                        and a2l_wp_id = v_qnaire_resp.a2l_wp_id 
                        and a2l_resp_id = v_qnaire_resp.a2l_resp_id
                  );
        else
            --if variant id is not null
            select count(1) into v_genereal_question_found
            from t_questionnaire_version 
            where  qnaire_id = v_general_ques_id 
                and qnaire_vers_id in 
                  ( 
                    select QNAIRE_VERS_ID 
                    from t_rvw_qnaire_response 
                    where pidc_vers_id = v_qnaire_resp.pidc_vers_id 
                        and variant_id = v_qnaire_resp.variant_id 
                        and a2l_wp_id = v_qnaire_resp.a2l_wp_id 
                        and a2l_resp_id = v_qnaire_resp.a2l_resp_id
                  );
                  
        end if;
        
        v_variant_id := v_qnaire_resp.variant_id;
        if v_genereal_question_found = 0 then
            --dbms_output.put_line('Pidc Version Id :  A2L WP ID : A2L RESP ID : Variant ID: '|| v_qnaire_resp.pidc_vers_id ||' '|| v_qnaire_resp.a2l_wp_id || ' ' || v_qnaire_resp.a2l_resp_id ||' ' ||v_qnaire_resp.variant_id );
            --to print the pidc version id
            dbms_output.put_line( 'not fount');
            --v_gen_qnaire_vers_id IN NUMBER,t_pidc_vers_id IN NUMBER,t_variant_id IN NUMBER,t_a2l_wp_id IN NUMBER ,t_a2l_resp_id IN NUMBER
            create_general_question(v_act_gen_qnaire_vers_id,v_qnaire_resp.pidc_vers_id,v_variant_id,v_qnaire_resp.a2l_wp_id,v_qnaire_resp.a2l_resp_id);
        end if ;
    END LOOP;
    
    pk_log.info('General question creation successful');
    PK_LOG.END_JOB;
    
EXCEPTION
        WHEN OTHERS THEN
            PK_LOG.ERROR('Error in general question creation : ',sqlcode,sqlerrm); 
            PK_LOG.END_JOB;
            RAISE_APPLICATION_ERROR(-20000, 'Error in Creation of General Questionnaire Response'); 
end;
/

--to check the total variants available for that pidc version in questionnaire response
select distinct qr.PIDC_VERS_ID,qr.VARIANT_ID,qr.a2l_wp_id,qr.a2l_resp_id from t_rvw_qnaire_response qr where pidc_vers_id = 1117440216;
--to check the whether the general question is created for that variants ,a2l wp and resp ids
select qr.PIDC_VERS_ID,qr.VARIANT_ID,qr.a2l_wp_id,qr.a2l_resp_id from t_rvw_qnaire_response qr where pidc_vers_id = 1117440216  and qnaire_vers_id = 1576715861;



spool off
