--------------------------------------------------------
-- Some Sample scripts to view job details
--   - to be used afte each migration step
--------------------------------------------------------

--step 1
select *
from v_log_msg_for_last_job
where job_name = 'Step1_Questionnaire_Data_Migration_Job';

--step 2
select *
from v_log_msg_for_last_job
where job_name = 'Step2_Questionnaire_Data_Migration_Job';

--step 3
select *
from v_log_msg_for_last_job
where job_name = 'Step3_Questionnaire_Data_Migration_Job';

--step 3 - migration progress
select TO_CHAR( SYSDATE, 'YYYY-MM-DD HH24:MI:SS' ) as "CUR_TIME", count_mig from (
    select count(1) as count_mig from v_log_msg_for_last_job
    where job_name = 'Step3_Questionnaire_Data_Migration_Job'
      and message like 'Started Questionnaire Migration for -> QNAIRE_RESP_ID %'
);

--Step 3C fixing general question log
select *
from v_log_msg_for_last_job
where job_name = 'Step3C_Fixing_script_for_general_question';

--Step 3C fixing merged qnaire resp versions log
select *
from v_log_msg_for_last_job
where job_name = 'Step3C_Fixing_script_for_merged_version';

--step 4
select *
from v_log_msg_for_last_job
where job_name = 'Step4_Questionnaire_Data_Migration_Job';




-- view list of jobs with the name (once jobs are executed)
Select distinct job_no 
from t_log_messages 
where job_name = '<job_name>' order by 1;

-- view detaisls of a specific job (once jobs are executed)
select to_char(time_stamp,'DD.MM.YYYY HH24:MI:SS') time_stamp
    , message
    , err_code
    , err_msg 
from t_log_messages 
where job_name = '<job_name>'
    and job_no = <number from prev statement>
order by message_id asc;


