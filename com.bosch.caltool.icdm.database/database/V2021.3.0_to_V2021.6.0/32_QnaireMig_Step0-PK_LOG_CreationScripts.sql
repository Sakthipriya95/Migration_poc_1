spool c:\temp\32_QnaireMig_Step0-PK_LOG_CreationScripts.log


--Creation of Step 1 qnaire data migration job
INSERT INTO t_jobs (
    job_id,
    job_name,
    responsible_mail_adr,
    send_mail_on_error,
    send_mail_on_finish,
    save_log_on_error_only,
    log_debug_messages,
    log_trace_messages
) 
	select
	    seqv_attributes.nextval,
	    'Step1_Questionnaire_Data_Migration_Job',
	    '',
	    'N',
	    'N',
	    'N',
	    'Y',
	    'Y'
	from dual;

--Creation of Step 2 qnaire data migration job
INSERT INTO t_jobs (
    job_id,
    job_name,
    responsible_mail_adr,
    send_mail_on_error,
    send_mail_on_finish,
    save_log_on_error_only,
    log_debug_messages,
    log_trace_messages
) 
	select
	    seqv_attributes.nextval,
	    'Step2_Questionnaire_Data_Migration_Job',
	    '',
	    'N',
	    'N',
	    'N',
	    'Y',
	    'Y'
	from dual;

--Creation of Step 3 qnaire data migration job
INSERT INTO t_jobs (
    job_id,
    job_name,
    responsible_mail_adr,
    send_mail_on_error,
    send_mail_on_finish,
    save_log_on_error_only,
    log_debug_messages,
    log_trace_messages
) 
	select
	    seqv_attributes.nextval,
	    'Step3_Questionnaire_Data_Migration_Job',
	    '',
	    'N',
	    'N',
	    'N',
	    'Y',
	    'Y'
	from dual;

--Creation of Step 3 Fixing general questions
INSERT INTO t_jobs (
    job_id,
    job_name,
    responsible_mail_adr,
    send_mail_on_error,
    send_mail_on_finish,
    save_log_on_error_only,
    log_debug_messages,
    log_trace_messages
) 
	select
	    seqv_attributes.nextval,
	    'Step3C_Fixing_script_for_general_question',
	    '',
	    'N',
	    'N',
	    'N',
	    'Y',
	    'Y'
	from dual;

--Creation of Step 3 Fixing merged versions
INSERT INTO t_jobs (
    job_id,
    job_name,
    responsible_mail_adr,
    send_mail_on_error,
    send_mail_on_finish,
    save_log_on_error_only,
    log_debug_messages,
    log_trace_messages
) 
	select
	    seqv_attributes.nextval,
	    'Step3C_Fixing_script_for_merged_version',
	    '',
	    'N',
	    'N',
	    'N',
	    'Y',
	    'Y'
	from dual;

--Creation of Step 4 qnaire data migration job
INSERT INTO t_jobs (
    job_id,
    job_name,
    responsible_mail_adr,
    send_mail_on_error,
    send_mail_on_finish,
    save_log_on_error_only,
    log_debug_messages,
    log_trace_messages
) 
	select
	    seqv_attributes.nextval,
	    'Step4_Questionnaire_Data_Migration_Job',
	    '',
	    'N',
	    'N',
	    'N',
	    'Y',
	    'Y'
	from dual;

commit;

spool off

