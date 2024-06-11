spool c:\temp\32_QnaireMig_Step3B-Validations1-A.log


-------------------------------------------------
-- Step 3 Validation Scripts
--Note : Do Not Run 32_qnaire_Step3C unless needed
-------------------------------------------------

-- Validation 1 :
-- 
-- Check if there are any qnaire responses yet to be mapped to wp-resp
-- Expected : no records should be returned.
--
select * from t_rvw_qnaire_response where a2l_wp_id is null or a2l_resp_id is null;


-- Validation 2 : 
--
-- Check if there are duplicate records for a single node in PIDC tree (PIDC Version + Varaint (or NO-Variant) + WP + RESP
-- Expected : no records should be returned.
--
select a2l_wp_id, a2l_resp_id,pidc_vers_id, variant_id, qnaire_vers_id, count(1) 
 from t_rvw_qnaire_response 
 group by a2l_wp_id, a2l_resp_id,pidc_vers_id, variant_id, qnaire_vers_id
 having count(1) > 1;


spool off
