spool c:\temp\32_QnaireMig_Step2B-Validation1.log


-------------------------------------------------
--Step 2 Validation Scripts - 1
-------------------------------------------------
-- Validation 1 : Script to validate wheather there is questionnaire version Id mismatch between the response and working set versions,
--     if there are any values found its not advisable to procced with further steps until it is fixed.
--
-- Expected : No records should be returned by the query
-------------------------------------------------

select 
      a.qnaire_resp_vers_id
    , a.qnaire_resp_id
    , a.qnaire_vers_id as qnaire_resp_vers_ver_id
    , b.qnaire_vers_id as qnaire_resp_ver_id
    , b.pidc_vers_id
    , a.CREATED_DATE
    , a.MODIFIED_DATE
    , a.REV_NUM 
from T_RVW_QNAIRE_RESP_VERSIONS a
    , t_rvw_qnaire_response b 
where a.qnaire_resp_id = b.qnaire_resp_id 
    and a.qnaire_vers_id <> b.qnaire_vers_id 
    and a.REV_NUM = 0;

-- Check if all responses have resp variants added
-- Note : If variant not available in resp, a rec would be created in T_RVW_QNAIRE_RESP_VARIANTSwith variant_id as null
--
-- Expected : no recs returned
select * from t_rvw_qnaire_response where qnaire_resp_id not in (select qnaire_resp_id from T_RVW_QNAIRE_RESP_VARIANTS)

-- Check if all responses have one resp version added
-- Expected : no recs returned
select * from t_rvw_qnaire_response where qnaire_resp_id not in (select qnaire_resp_id from T_RVW_QNAIRE_RESP_Versions)

-- check one resp_version /resp
-- Expected : no recs returned
select qnaire_resp_id, count(1) from T_RVW_QNAIRE_RESP_Versions
group by qnaire_resp_id
having count(1) > 1

spool off
