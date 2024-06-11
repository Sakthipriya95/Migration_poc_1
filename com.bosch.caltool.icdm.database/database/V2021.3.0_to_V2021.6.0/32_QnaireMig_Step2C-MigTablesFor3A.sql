spool c:\temp\32_QnaireMig_Step2C-MigTablesFor3A.log

--------------------------------------------------------------------------------------------
-- New tables tmp_pidc_vers_qnaire, t_temp_rvwwpresp_wp_assignment for migration activities
-- Identifies the questionnaires to be duplicated at the WP resp level
-- Note : as part of migration, the table is dropped and created again
--------------------------------------------------------------------------------------------

set serveroutput on size unlimited

WHENEVER SQLERROR EXIT SQL.SQLCODE ROLLBACK;

drop table tmp_pidc_vers_qnaire;

create table tmp_pidc_vers_qnaire
as
  select distinct pidc_vers_id, result_id, icdm_qnaire_config, nvl(ICDM_QNAIRE_CONFIG_ID,787372417) ICDM_QNAIRE_CONFIG_ID, fc2wp.version_id fc2wp_ver_id
  from v_cdr_results 
      join v_pidc_versions using(pidc_vers_id)
      join V_FC2WP_VERSIONS fc2wp on (fc2wp.div_value_id = nvl(v_pidc_versions.ICDM_QNAIRE_CONFIG_ID,787372417) and fc2wp.active_flag = 'Y')
;

drop table t_temp_rvwwpresp_wp_assignment;  

create table t_temp_rvwwpresp_wp_assignment 
as
    select distinct 
       RVW_WP_RESP_ID,
       fc2wp.wp_div_id
    from t_rvw_parameters params
        join tmp_pidc_vers_qnaire pidc_vers_qnaire on (params.result_id = pidc_vers_qnaire.result_id )
        join t_rvw_functions  fc on (fc.rvw_fun_id = params.rvw_fun_id)
        join t_fc2wp_mapping fc2wp on (fc2wp.fcwp_ver_id = pidc_vers_qnaire.FC2WP_VER_ID and fc2wp.function_id = fc.function_id)
;  

drop table t_temp_qnaireresp_wpresp;

CREATE TABLE t_temp_qnaireresp_wpresp
  AS (
        select distinct qnaire_resp_id, wp_div_id, a2l_wp_id, a2l_resp_id 
          from t_rvw_wp_resp
          join t_temp_rvwwpresp_wp_assignment using (RVW_WP_RESP_ID) 
          join t_rvw_qnaire_results using(result_id) 
          join v_qn_rvw_response using(qnaire_resp_id, WP_DIV_ID)
        union all
        -- Qnaire Responses not assigned to a WP through FC2WP. Reason: Mismatch between selected WP  
        -- Solution: In this case we assign the QNaire to all A2L/WP combinations
        select distinct qn.qnaire_resp_id, qn.wp_div_id, wp_res.a2l_wp_id, wp_res.a2l_resp_id
          from v_qn_rvw_response qn
          join t_rvw_qnaire_results res on (qn.QNAIRE_RESP_ID = res.QNAIRE_RESP_ID)
          join t_rvw_wp_resp wp_res on (wp_res.RESULT_ID = res.RESULT_ID)
        where qn.qnaire_resp_id not in ( select distinct qnaire_resp_id 
                                            from t_rvw_wp_resp
                                            join t_temp_rvwwpresp_wp_assignment using (RVW_WP_RESP_ID) 
                                            join t_rvw_qnaire_results using(result_id) 
                                            join v_qn_rvw_response using(qnaire_resp_id, WP_DIV_ID) )
);


spool off
