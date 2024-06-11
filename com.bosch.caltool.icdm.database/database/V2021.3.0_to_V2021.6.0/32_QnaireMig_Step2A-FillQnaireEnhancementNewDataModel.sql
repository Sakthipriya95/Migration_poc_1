spool c:\temp\32_QnaireMig_Step2A-FillQnaireEnhancementNewDataModel.log

-----------------------------------------------------------------
-- Script is to : 
--   a) fill the new data model (res_version, resp_variants)
--   b) update answers with latest version ID
--
-- IMPORTANT : before commiting, run the next validation script(Step2B_Validations.sql) !!!
-----------------------------------------------------------------

set serveroutput on size unlimited

WHENEVER SQLERROR EXIT SQL.SQLCODE ROLLBACK;

declare 

    --step 1 variables start
    --PIDC Version ID
    v_pidc_vers_id t_rvw_qnaire_response.PIDC_VERS_ID%type;  
    --Variant ID
    v_variant_id t_rvw_qnaire_response.VARIANT_ID%type;  
    --Qnaire Resp ID
    v_qnaire_res_id t_rvw_qnaire_response.qnaire_resp_id%type;  
    --QNAIRE_VERS_ID
    v_qnaire_vers_id t_rvw_qnaire_response.QNAIRE_VERS_ID%type;  
    
    v_qnaire_resp_vers_id T_RVW_QNAIRE_RESP_VERSIONS.QNAIRE_RESP_VERS_ID%type;

    --to hold the count of already available data in versions and variant table
    any_qnaire_variant_found number;
    any_qnaire_version_found number;
    
    v_gen_qnaire_vers_id number;
    
    v_general_ques_id number;
    
	--icdm:pidvid,14241674831
	--icdm:pidvid,14445559537
	--icdm:pidvid,1423099081
	--Input PIDC VERSIon
	--V_temp_test_pidc_vers_id number:=14452963331;
	
     --cursor to hold the qnaire resp data for Creating Questionnaire Response,Versions and Variants based on new questionnaire datamodel
    CURSOR qnaire_resp_cur IS 
        SELECT DISTINCT qr.qnaire_resp_id ,qr.PIDC_VERS_ID,qr.VARIANT_ID,qr.QNAIRE_VERS_ID
        FROM t_rvw_qnaire_response qr
        WHERE
            qr.qnaire_resp_id in (select qnaire_resp_id 
                                    from t_rvw_qnaire_results
									--where result_id in (select result_id 
                                    --                    from t_rvw_results 
                                    --                   where pidc_a2l_id in (select pidc_a2l_id from t_pidc_a2l where pidc_vers_id = V_temp_test_pidc_vers_id))
								);

BEGIN 
	PK_LOG.START_NEW_JOB('Step2_Questionnaire_Data_Migration_Job');
    
    --getting general questionnaire version id
    select param_value into v_general_ques_id from tabv_common_params where param_id = 'GENERAL_QNAIRE_ID';
            
	select qnaire_vers_id into v_gen_qnaire_vers_id from t_questionnaire_version where qnaire_id = v_general_ques_id and active_flag = 'Y';
	pk_log.info('Active General Questionnaire Version ID :'||v_gen_qnaire_vers_id);
    
    --end of fetch general question version id
    
    pk_log.info('Started Creating Questionnaire Response,Versions and Variants based on new questionnaire datamodel ');
    open qnaire_resp_cur;
    LOOP
        begin
        --fetching from the cursor
            FETCH qnaire_resp_cur into v_qnaire_res_id,v_pidc_vers_id, v_variant_id, v_qnaire_vers_id ; 
            EXIT WHEN qnaire_resp_cur%notfound;
            
            --to check if there is already data available for the qnaire resp id
            --QNAIRE VARIANT COUNT
            select count(1) into any_qnaire_variant_found 
            FROM t_rvw_qnaire_resp_variants 
            WHERE qnaire_resp_id = v_qnaire_res_id;
            
            --QNAIRE VERSION COUNT        
            select count(1) into any_qnaire_version_found 
            FROM t_rvw_qnaire_resp_versions 
            WHERE qnaire_resp_id = v_qnaire_res_id;
             
            --to check if there is already variant available for that questionnaire response
            if (any_qnaire_variant_found = 0) then				
                Insert into T_RVW_QNAIRE_RESP_VARIANTS 
                    (PIDC_VERS_ID,VARIANT_ID,QNAIRE_RESP_ID) 
                values 
                (
                    v_pidc_vers_id,
                    v_variant_id,
                    v_qnaire_res_id
                );
                pk_log.info('Inserted in T_RVW_QNAIRE_RESP_VARIANTS for  Qnaire_Resp_id : '|| v_qnaire_res_id);
            end if;
            
            --to check if there is already version available for that questionnaire response
            if (any_qnaire_version_found = 0) then 
                insert into t_rvw_qnaire_resp_versions 
                  (QNAIRE_RESP_ID,NAME,DESCRIPTION,REV_NUM,QNAIRE_VERS_ID,QNAIRE_VERS_STATUS) 
                values 
                  (
                    v_qnaire_res_id,
                    'Working Set',
                    'Created during migration',
                    0,
                    v_qnaire_vers_id,
                    '-'
                  )RETURNING QNAIRE_RESP_VERS_ID INTO v_qnaire_resp_vers_id ;
                
                pk_log.info('Inserted in t_rvw_qnaire_resp_versions for  Qnaire_Resp_id : '|| v_qnaire_res_id);
                
                --to update the questionniare resp version id in T_RVW_QNAIRE_ANSWER table
                pk_log.info( 'update T_RVW_QNAIRE_ANSWER set qnaire_resp_vers_id ='|| v_qnaire_vers_id ||' where QNAIRE_RESP_ID ='||v_qnaire_res_id||';');
                update T_RVW_QNAIRE_ANSWER
                set qnaire_resp_vers_id = v_qnaire_resp_vers_id 
                where QNAIRE_RESP_ID =v_qnaire_res_id;
                
                pk_log.info('Updated T_RVW_QNAIRE_ANSWER with latest qnaire_resp_vers_id');
            end if;
                
        EXCEPTION
            WHEN OTHERS THEN
                PK_LOG.ERROR('Error in Questionnaire_Data_Migration_Job when creating data in questionnaire datamodel : ',sqlcode,sqlerrm); 
                PK_LOG.END_JOB;
                RAISE_APPLICATION_ERROR(-20000, 'Error in Questionnaire_Data_Migration_Job when creating data in questionnaire datamodel'); 
        end;
    END LOOP;
    close qnaire_resp_cur;
    
    pk_log.info('End of Creating Questionnaire Response,Versions and Variants based on new questionnaire datamodel ');
	PK_LOG.END_JOB;
    
EXCEPTION 
    when others then
        PK_LOG.ERROR('Error in Step 2 Questionnaire Data Migration Job',sqlcode,sqlerrm); 
        PK_LOG.END_JOB;
        RAISE_APPLICATION_ERROR(-20000, 'Error in Step 2 Questionnaire Data Migration Job'); 
end;
/

spool off
