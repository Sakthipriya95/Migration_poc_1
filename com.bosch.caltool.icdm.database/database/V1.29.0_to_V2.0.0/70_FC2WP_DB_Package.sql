create or replace package pk_utils
as
  function get_common_param(a_param_id varchar2) return varchar2 deterministic;
end;
/

create or replace package body pk_utils
as
  function get_common_param(a_param_id varchar2) return varchar2 deterministic
  is
    lo_param_value tabv_common_params.param_value%type;
  begin
    select param_value
      into lo_param_value
      from tabv_common_params
     where param_id = a_param_id;

     return lo_param_value;
  exception 
    when no_data_found then
        raise_application_error(-20998, 'The constant ''' || a_param_id || ''' is not existing in tabv_common_params.' );
    when others then
        raise;
  end;
end;
/


-- in k5esk_villa
grant select on ta2l_modules to dgs_icdm, dgs_icdm_test;
grant select on ta2l_fileinfo to dgs_icdm, dgs_icdm_test;
grant select on ta2l_functions to dgs_icdm, dgs_icdm_test;

-- In dgs_icdm
create or replace procedure p_send_mail(a_recipient varchar2, a_subject varchar2, a_mailtext varchar2  )
is
  v_boundary  VARCHAR2 (256) := '-----090303020209010600070908';
  
  v_From      VARCHAR2(80) := 'iCDM-Hotline.Clearing@de.bosch.com';
  v_Mail_Host VARCHAR2(30) := 'rb-smtp-int.bosch.com';
  
  v_Recipient varchar2(500) := a_recipient;
  v_Subject   VARCHAR2(80)  := a_subject;
  
  v_Mail_Conn utl_smtp.Connection;
  crlf        VARCHAR2(2)  := chr(13)||chr(10);
BEGIN
 v_Mail_Conn := utl_smtp.Open_Connection(v_Mail_Host, 25);
 utl_smtp.Helo(v_Mail_Conn, v_Mail_Host);
 utl_smtp.Mail(v_Mail_Conn, v_From);
 utl_smtp.Rcpt(v_Mail_Conn, v_Recipient);
 
  utl_smtp.Data(v_Mail_Conn,
   'MIME-Version: 1.0' || crlf ||
   'Content-type: text/html' || crlf ||
   'Date: '   || to_char(sysdate, 'Dy, DD Mon YYYY hh24:mi:ss') || crlf ||
   'From: '   || v_From || crlf ||
   'Subject: '|| v_Subject || crlf ||
   'To: '     || v_Recipient || crlf ||
   crlf || a_mailtext || crlf
 );
 
 utl_smtp.Quit(v_mail_conn);
EXCEPTION
 WHEN utl_smtp.Transient_Error OR utl_smtp.Permanent_Error then
   raise_application_error(-20000, 'Unable to send mail', TRUE);
END;
/

CREATE SEQUENCE  "SEQV_LOG"  MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 1 CACHE 20 NOORDER  NOCYCLE;
CREATE SEQUENCE  "SEQV_DWH"  MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 1 CACHE 20 NOORDER  NOCYCLE ;

CREATE TABLE "T_JOBS" 
   (    "JOB_ID" NUMBER, 
    "JOB_NAME" VARCHAR2(200 BYTE), 
    "RESPONSIBLE_MAIL_ADR" VARCHAR2(2000 BYTE), 
    "SEND_MAIL_ON_ERROR" VARCHAR2(1 BYTE) DEFAULT 'N' NOT NULL ENABLE, 
    "LAST_START_TIME" DATE, 
    "LAST_END_TIME" DATE, 
    "LAST_RUN_ENDED_WITH_SUCCESS" VARCHAR2(1 BYTE) DEFAULT 'Y' NOT NULL ENABLE, 
    "SEND_MAIL_ON_FINISH" VARCHAR2(1 BYTE) DEFAULT 'N' NOT NULL ENABLE, 
    "SAVE_LOG_ON_ERROR_ONLY" VARCHAR2(1 BYTE) DEFAULT 'N' NOT NULL ENABLE, 
    "LOG_DEBUG_MESSAGES" VARCHAR2(1 BYTE) DEFAULT 'N' NOT NULL ENABLE, 
    "LOG_TRACE_MESSAGES" VARCHAR2(1 BYTE) DEFAULT 'N' NOT NULL ENABLE, 
     CHECK (send_mail_on_error in ('Y','N')) ENABLE, 
     CHECK (last_run_ended_with_success in ('Y','N')) ENABLE, 
     PRIMARY KEY ("JOB_ID")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "DGS_ICDM_TS"  ENABLE, 
     UNIQUE ("JOB_NAME")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "DGS_ICDM_TS"  ENABLE, 
     CHECK (log_trace_messages in ('Y','N')) ENABLE
   ) SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 
 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "DGS_ICDM_TS" ;

CREATE OR REPLACE TRIGGER "TRG_T_JOBS_INS" 
BEFORE INSERT ON t_jobs
FOR EACH ROW
Begin
      IF :new.job_id is null THEN
        SELECT seqv_dwh.nextval INTO :new.job_id FROM DUAL;
    END IF;
END;
/

ALTER TRIGGER "TRG_T_JOBS_INS" ENABLE;


  CREATE TABLE "T_LOG_MESSAGES" 
   (    "MESSAGE_ID" NUMBER NOT NULL ENABLE, 
    "JOB_NO" NUMBER NOT NULL ENABLE, 
    "STEP_NO" NUMBER NOT NULL ENABLE, 
    "TIME_STAMP" DATE DEFAULT sysdate NOT NULL ENABLE, 
    "JOB_NAME" VARCHAR2(200 BYTE) NOT NULL ENABLE, 
    "TYPE" VARCHAR2(1 BYTE) DEFAULT 'I' NOT NULL ENABLE, 
    "MESSAGE" VARCHAR2(4000 BYTE), 
    "ERR_CODE" NUMBER, 
    "ERR_MSG" VARCHAR2(4000 BYTE), 
     CONSTRAINT "T_LOG_MESSAGES_PK" PRIMARY KEY ("MESSAGE_ID")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "DGS_ICDM_TS"  ENABLE, 
     CONSTRAINT "C_CHECK_TYPE" CHECK (type in ('E','I','D', 'T')) ENABLE
   ) SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 
 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "DGS_ICDM_TS" ;

  CREATE UNIQUE INDEX "IDX_LOG_MESSAGES_UNIQUE" ON "T_LOG_MESSAGES" ("JOB_NO", "STEP_NO") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "DGS_ICDM_TS" ;

  CREATE OR REPLACE TRIGGER "TRG_LOG_MESSAGES_INS" 
BEFORE INSERT ON t_log_messages
FOR EACH ROW
Begin
      IF :new.message_id is null THEN
        SELECT seqv_log.nextval INTO :new.message_id FROM DUAL;
    END IF;
END;
/
ALTER TRIGGER "TRG_LOG_MESSAGES_INS" ENABLE;



create or replace package pk_log
as

  v_enable_dbms_output boolean := true;

  procedure start_new_job(job_name varchar2);
  
  procedure info(message varchar2);
  
  procedure debug(message varchar2);
  
  procedure trace(message varchar2);
  
  procedure error(message varchar2);
  
  procedure error(message varchar2, error_code number, error_message varchar2);
  
  procedure end_job;
end;
/

create or replace package body pk_log
as
  CO_INITIALIZATION_STEP constant number := 0;  

  v_initialized boolean := false;
  v_job_no number;
  v_step_no number := CO_INITIALIZATION_STEP;
  v_job_name varchar2(200);
  v_job_successful_ended varchar2(1) := 'Y';
  current_job   t_jobs%ROWTYPE;
  
  logger_not_initialized EXCEPTION;
  PRAGMA EXCEPTION_INIT(logger_not_initialized, -20888);
  
  job_not_existing EXCEPTION;
  PRAGMA EXCEPTION_INIT(job_not_existing, -20889);
  
  procedure create_entry(message varchar2, error_code number, error_message varchar2, log_type varchar2);
  procedure write_to_log_table(message varchar2, error_code number, error_message varchar2, log_type varchar2);
  procedure write_to_dbms_output(message varchar2, error_code number, error_message varchar2, log_type varchar2);
  procedure write_error_to_job_table(log_type varchar2);
  procedure write_start_time_to_job_table;
  procedure write_finish_state_to_log_tab;
  procedure write_end_time_to_job_table;
  procedure delete_last_log_entries;
  procedure load_job(a_job_name varchar2);
  procedure send_finish_mail;
  procedure send_error_mail(a_message varchar2, a_error_code varchar2, a_error_message varchar2);
  
  function get_step_no return number;
  function get_job_no return number;
  function get_job_name return varchar2;
  function is_logger_initialized return boolean;
  function f_should_send_mail_on_finish return boolean;
  function f_should_send_mail_on_error return boolean;
  function f_is_log_type_an_error(log_type varchar2) return boolean;
  function f_should_log_debug_msg return boolean;
  function f_should_log_trace_msg return boolean;
  function f_save_log_on_error_only return boolean;
  function f_finished_wo_error return boolean;

  procedure start_new_job(job_name varchar2)
  is
    TYPE type_jobs IS TABLE OF t_jobs%ROWTYPE  INDEX BY PLS_INTEGER;
    v_jobs type_jobs;
  begin
    v_job_no := seqv_log.nextval;
    v_job_name := job_name;
    v_step_no := 0;
    v_initialized := true;
    v_job_successful_ended := 'Y';
    
    load_job(job_name);
    write_start_time_to_job_table;
    
    info('Start job: ' || v_job_name);
  end;
  
  procedure info(message varchar2) 
  is
  begin
    create_entry(message,null,null,'I');
  end;
  
  procedure debug(message varchar2) 
  is
  begin
    if f_should_log_debug_msg then
      create_entry(message,null,null,'D');
    end if;
  end;
  
  procedure trace(message varchar2) 
  is
  begin
    if f_should_log_trace_msg then
      create_entry(message,null,null,'T');
    end if;
  end;
  
  procedure error(message varchar2) 
  is
  begin
    create_entry(message,null,null,'E');
  end;
  
  procedure error(message varchar2, error_code number, error_message varchar2) 
  is
  begin
    create_entry(message,error_code,error_message,'E');
  end;
  
  procedure end_job
  is
  begin
    info('End job: ' || v_job_name);
    write_finish_state_to_log_tab;
    write_end_time_to_job_table;
    
    if f_should_send_mail_on_finish then
      send_finish_mail;
    end if;
    
    if f_save_log_on_error_only then
      delete_last_log_entries;
    end if;
    
    v_step_no := CO_INITIALIZATION_STEP;
    v_initialized := false;
  end;
  
  procedure delete_last_log_entries is
    PRAGMA AUTONOMOUS_TRANSACTION;
    
    v_cur_job_no number := get_job_no;
  begin
    if f_finished_wo_error then
      
      delete from t_log_messages
        where job_no = v_cur_job_no;
    end if;
    
    commit;
  exception when others then
      rollback;
  end;
  
  function f_finished_wo_error return boolean
  is
  begin
    return (current_job.LAST_RUN_ENDED_WITH_SUCCESS = 'Y');
  end;
  
  
  function f_should_send_mail_on_finish return boolean
  is
  begin
    return (current_job.send_mail_on_finish = 'Y');
  end;
  
  function f_should_send_mail_on_error return boolean
  is
  begin
    return (current_job.send_mail_on_error = 'Y');
  end;
  
  function f_is_log_type_an_error(log_type varchar2) return boolean
  is
  begin
    return (log_type = 'E');
  end;
  
  function f_should_log_debug_msg return boolean
  is
  begin
    return (current_job.log_debug_messages = 'Y');
  end;
  
  function f_should_log_trace_msg return boolean
  is
  begin
    return (current_job.log_trace_messages = 'Y');
  end;
  
  function f_save_log_on_error_only return boolean
  is
  begin
    return (current_job.save_log_on_error_only = 'Y');
  end;  
  
  procedure create_entry(message varchar2, error_code number, error_message varchar2, log_type varchar2)
  is
   PRAGMA AUTONOMOUS_TRANSACTION;
   
    v_cur_step_no number := get_step_no;
    v_cur_job_no number := get_job_no;
    v_cur_job_name varchar2(100) := get_job_name;
  begin
    if not is_logger_initialized then
      RAISE_APPLICATION_ERROR(-20888, 'Logger has to be initialized with ''start_new_job''');
    end if;
    
    write_to_dbms_output(message , error_code , error_message , log_type );
    write_to_log_table(message , error_code , error_message , log_type );
    write_error_to_job_table(log_type);
    
    if (f_should_send_mail_on_error and f_is_log_type_an_error(log_type)) then
      send_error_mail(message,error_code,error_message);
    end if;
  exception  when logger_not_initialized then
      raise;
  end;
  
  procedure write_to_log_table(message varchar2, error_code number, error_message varchar2, log_type varchar2)
  is
   PRAGMA AUTONOMOUS_TRANSACTION;
   
    v_cur_step_no number := get_step_no;
    v_cur_job_no number := get_job_no;
    v_cur_job_name varchar2(100) := get_job_name;
  begin
    insert into t_log_messages
      ( job_no
      , step_no
      , time_stamp
      , job_name
      , type
      , message
      , err_code
      , err_msg )
      select
      v_cur_job_no
      , v_cur_step_no
      , sysdate
      , v_cur_job_name
      , log_type
      , substr(message,1,4000)
      , error_code
      , substr(error_message,1,4000)
      from dual;
    
    commit;
  exception when others then
      rollback;
  end;
  
  procedure write_to_dbms_output(message varchar2, error_code number, error_message varchar2, log_type varchar2)
  is
  begin
    if pk_log.v_enable_dbms_output then
      DBMS_OUTPUT.PUT_LINE(to_char(sysdate,'DD.MM.YYYY HH24:MI') || ': ' || log_type || ': ' || message || ' ' || error_code || ' ' || error_message);  
    end if;
  exception when others then
      null;
  end;
  
  procedure write_error_to_job_table(log_type varchar2)
  is
   PRAGMA AUTONOMOUS_TRANSACTION;
  begin
    if log_type = 'E' then
      v_job_successful_ended := 'N';
      current_job.LAST_RUN_ENDED_WITH_SUCCESS := 'N';
    
      update t_jobs
         set LAST_RUN_ENDED_WITH_SUCCESS = v_job_successful_ended
       where job_id = current_job.job_id;
    end if;
          
    commit;
  exception when others then
      commit;
  end;
  
  procedure write_finish_state_to_log_tab
  is
   PRAGMA AUTONOMOUS_TRANSACTION;
  begin
    update t_jobs
       set LAST_RUN_ENDED_WITH_SUCCESS = v_job_successful_ended
     where job_id = current_job.job_id;
   
    commit;
  exception when others then
      commit;
  end;
  
  procedure write_start_time_to_job_table
  is
   PRAGMA AUTONOMOUS_TRANSACTION;
  begin
    update t_jobs
       set last_start_time = sysdate
     where job_id = current_job.job_id;
    
    commit;
  exception when others then
      rollback;
  end;
  
  procedure write_end_time_to_job_table
  is
   PRAGMA AUTONOMOUS_TRANSACTION;
  begin
    dbms_output.put_line('Write end time to' ||current_job.job_id);
  
    update t_jobs
       set last_end_time = sysdate
     where job_id = current_job.job_id;
    
    commit;
  exception when others then
      rollback;
  end;
  
  function get_step_no return number
  is
    v_lockhandle varchar2(200);
    v_result number;
  begin
    dbms_lock.allocate_unique('lock_increase_step', v_lockhandle);
    v_step_no := v_step_no + 1;
    v_result := dbms_lock.release(v_lockhandle);
    
    return v_step_no;
  end;
  
  function get_job_no return number
  is
  begin
    return v_job_no;
  end;
  
  function get_job_name return varchar2
  is
  begin
    return v_job_name;
  end;
  
  function is_logger_initialized return boolean
  is
  begin
    return v_initialized;
  end;
  
  procedure load_job(a_job_name varchar2)
  is
  begin
      select * 
        into current_job 
        from t_jobs 
       where job_name = a_job_name;
  exception
    when no_data_found then
      RAISE_APPLICATION_ERROR(-20889, 'The given job is not existing. Please check the name or add the job in table t_jobs.');
  end;
  
  procedure send_finish_mail
  is 

  lo_text varchar2(32000);
  lo_overall_status varchar2(500);
  rec_job_result t_jobs%rowtype;
  v_job_name varchar2(200) := get_job_name();
  
  begin
    select *
      into rec_job_result
      from t_jobs
    where job_name = v_job_name;
    
    lo_overall_status := 'Job Name: ' || rec_job_result.job_name || '<br>' 
                       || 'Start Time: ' || to_char(rec_job_result.last_start_time,'DD.MM.YYYY HH24:MI:SS') || '<br>' 
                       || 'End Time: ' || to_char(rec_job_result.last_end_time,'DD.MM.YYYY HH24:MI:SS') || '<br>'
                       || 'Succesfully Ended: ' || rec_job_result.last_run_ended_with_success || '<br>';
  
      lo_text := lo_text || '<tr>' || '<td>' || 'Time Stamp' || '</td>'
                         || '<td>' || 'Log Message' || '</td>'
                         || '<td>' || 'Error Code' || '</td>'
                         || '<td>' || 'Error Message' || '</td>';
  
    for rec in (select to_char(time_stamp,'DD.MM.YYYY HH24:MI:SS') time_stamp, message, err_code, err_msg 
                   from t_log_messages 
                  where job_name = v_job_name
                    and job_no = (Select max(job_no) from t_log_messages where job_name = v_job_name)
                    and type in ('I','E')
                    order by message_id asc)
    loop
      lo_text := lo_text || '<tr>' || '<td>' || rec.time_stamp || '</td>'
                         || '<td>' || rec.message || '</td>'
                         || '<td>' || rec.err_code || '</td>'
                         || '<td>' || rec.err_msg || '</td>';
    end loop;
    lo_text := '<html><body>' || lo_overall_status || '<br><table>' || lo_text || '</table></body></html>';
    
    p_send_mail(rec_job_result.responsible_mail_adr ,'Job ' || v_job_name || ' finished',lo_text);
  end;
  
  procedure send_error_mail(a_message varchar2, a_error_code varchar2, a_error_message varchar2)
  is 

  lo_overall_status varchar2(32000);
  rec_job_result t_jobs%rowtype;
  v_job_name varchar2(200) := get_job_name();
  
  begin
    select *
      into rec_job_result
      from t_jobs
    where job_name = v_job_name;
    
    lo_overall_status := 'Job Name: ' || rec_job_result.job_name || '<br>' 
                       || 'encountered an error! ' || '<br>'
                       || 'Message: '       || substr(a_message,1,20000) || '<br>'
                       || 'Error Code: '    || nvl( a_error_code, 'Not specified' ) || '<br>'
                       || 'Error Message: ' || substr(nvl( a_error_message, 'Not specified' ),1,10000);
    
    p_send_mail(rec_job_result.responsible_mail_adr ,'Warning: Error in Job ' || v_job_name || '.',lo_overall_status);
  end;
end;
/


  CREATE OR REPLACE FORCE VIEW "V_PIDC_VERSIONS" ("PIDC_ID", "PIDC_NAME", "PIDC_VERS_ID", "PRO_REV_ID", "PIDC_VERS_NAME", "PIDC_VERS_DESC_ENG", "ACTIVE_VERSION", "IS_TESTCUSTOMER", "CREATED_USER", "PIDC_CREATED_DATE", "PIDC_VERS_CREATED_DATE", "DELETED_FLAG", "CUSTOMER_BRAND_ID", "CUSTOMER_BRAND", "ECU_USED_FOR_ID", "ECU_USED_FOR", "ECU_USED_IN_ID", "ECU_USED_IN", "ECU_GENERATION_ID", "ECU_GENERATION", "APRJ_NAME_VALUE_ID", "APRJ_ID_IN_VCDM", "APRJ_NAME_IN_VCDM", "LAST_TRANSFER_TO_VCDM_ON", "LAST_TRANSFER_TO_VCDM_BY", "CAL_PROJECT_ORGA_ID", "CAL_PROJECT_ORGA") AS 
  SELECT pidc.project_id pidc_id
       , pidc_name.textvalue_eng pidc_name
       , pidc_version.pidc_vers_id
       , pidc_version.pro_rev_id
  , pidc_version.vers_name pidc_vers_name
  , pidc_version.vers_desc_eng pidc_vers_desc_eng
  , CASE
      WHEN pidc_version.pro_rev_id = pidc.pro_rev_id
      THEN 'Y'
      ELSE 'N'
    END active_version
  , case
      when customer_brand.value_id = 2332 then 'Y'
      else 'N'
    end is_testcustomer
  , pidc.created_user
  , pidc.created_date
  , pidc_version.created_date
  , pidc_name.deleted_flag
  , customer_brand.value_id customer_brand_id
  , customer_brand.value customer_brand
  , ecu_used_for.value_id ecu_used_for_id
  , ecu_used_for.value ecu_used_for
  , ecu_used_in.value_id ecu_used_in_id
  , ecu_used_in.value ecu_used_in
  , ecu_generation.value_id ecu_generation_id
  , ecu_generation.value ecu_generation
  , aprj_name_vcdm.value_id aprj_name_value_id
  , pidc.aprj_id         aprj_id_in_vcdm
  , aprj_name_vcdm.value aprj_name_in_vcdm
  , pidc.vcdm_transfer_date last_transfer_to_vcdm_on
  , pidc.vcdm_transfer_user last_transfer_to_vcdm_by
  , cal_project_orga.value_id cal_project_orga_id
  , cal_project_orga.value cal_project_orga
  FROM tabv_projectidcard pidc
  JOIN t_pidc_version pidc_version
    ON ( pidc_version.project_id = pidc.project_id )
  LEFT JOIN tabv_attr_values pidc_name
    ON (pidc.value_id = pidc_name.value_id )
  left join v_project_attr_values customer_brand
     on (customer_brand.pidc_vers_id = pidc_version.pidc_vers_id
         and customer_brand.attr_id = 36 -- Customer
      )
  left join v_project_attr_values ecu_used_for
     on (ecu_used_for.pidc_vers_id = pidc_version.pidc_vers_id
         and ecu_used_for.attr_id = 2225 -- ECU used for
      )
  left join v_project_attr_values ecu_used_in
     on (ecu_used_in.pidc_vers_id = pidc_version.pidc_vers_id
         and ecu_used_in.attr_id = 2232 -- ECU used in
      )         
  left join v_project_attr_values ecu_generation
     on (ecu_generation.pidc_vers_id = pidc_version.pidc_vers_id
         and ecu_generation.attr_id = 197 -- ECU Generation
      )
  left join v_project_attr_values aprj_name_vcdm
     on (aprj_name_vcdm.pidc_vers_id = pidc_version.pidc_vers_id
         and aprj_name_vcdm.attr_id = 2742 -- APRJ Name in vCDM
      )
  left join v_project_attr_values cal_project_orga
     on (cal_project_orga.pidc_vers_id = pidc_version.pidc_vers_id
         and cal_project_orga.attr_id = 787372416 -- Division
      );


create table t_dwh_fc2wp_rel_function
(
  function_id         number
, function_name       varchar2(255)
, division_id         number  
, relevant            varchar2(1)  default 'Y'  not null check(relevant in ('Y','N'))   
, function_name_upper varchar2(255) GENERATED ALWAYS AS (upper(function_name)) VIRTUAL
)
/

create index idx_dwh_fc2wp_func_name_upper on t_dwh_fc2wp_rel_function
(
  function_name_upper
);

create index idx_dwh_fc2wp_function_id on t_dwh_fc2wp_rel_function
(
  function_id
);

create or replace package pk_dwh_fc2wp
as
  procedure mark_usage_of_func_in_icdm_a2l;
end;
/

create or replace package body pk_dwh_fc2wp
as
    
  procedure upd_all_func_as_not_relevant;
  procedure upd_used_functions_as_relevant;
  procedure add_function_id;
  procedure upd_rel_flag_in_fc2wp_lists;

  procedure mark_usage_of_func_in_icdm_a2l
  is
  begin 
    pk_log.start_new_job('pk_dwh_fc2wp');

    upd_all_func_as_not_relevant;
    upd_used_functions_as_relevant;
    add_function_id;
    upd_rel_flag_in_fc2wp_lists;

    pk_log.end_job;
  exception 
    when others then
      pk_log.error('Error in pk_dwh_fc2wp.mark_usage_of_func_in_icdm_a2l', sqlcode, sqlerrm);
      pk_log.end_job;
  end;  

  procedure upd_all_func_as_not_relevant
  is
  begin
    pk_log.debug('Start upd_all_func_as_not_relevant');

    delete from t_dwh_fc2wp_rel_function;

    pk_log.debug('Updated: ' || sql%rowcount); 
    pk_log.debug('End upd_all_func_as_not_relevant');
  exception 
    when others then
      PK_LOG.ERROR('Error in pk_dwh_fc2wp.upd_all_func_as_not_relevant',sqlcode,sqlerrm);
      raise;        
  end;

  procedure upd_used_functions_as_relevant
  is
  begin
    pk_log.debug('Start upd_used_functions_as_relevant');

    insert into t_dwh_fc2wp_rel_function (function_name,division_id)
    select distinct upper(f.name) function_name
         , pidc.division_id
      from ta2l_modules   mo
         , ta2l_fileinfo  fi
         , ta2l_functions f
         , ( select distinct pidc_versions.cal_project_orga_id division_id, a2l.a2l_file_id
               from v_pidc_versions pidc_versions
               join t_pidc_a2l      a2l on ( a2l.pidc_vers_id = pidc_versions.pidc_vers_id )
              where is_testcustomer = 'N' 
                and deleted_flag = 'N'
                and cal_project_orga_id is not null ) pidc
     where mo.file_id = fi.id
       and f.module_id = mo.module_id
       and fi.id = pidc.a2l_file_id;
    
    pk_log.trace('Inserted functions in t_dwh_fc2wp_rel_function: ' || sql%rowcount);      
    pk_log.debug('End upd_used_functions_as_relevant');
  exception 
    when others then
      PK_LOG.ERROR('Error in pk_dwh_fc2wp.upd_used_functions_as_relevant',sqlcode,sqlerrm);
      raise;            
  end;

  procedure add_function_id
  is
  begin
    pk_log.debug('Start add_function_id');

    update t_dwh_fc2wp_rel_function fc2wp
       set function_id = ( select id
                             from t_functions fun
                            where fc2wp.function_name_upper = fun.function_name_upper
                              and fun.relevant_name = 'Y')
     where function_id is null;

    pk_log.trace('Updated function ID in t_dwh_fc2wp_rel_function: ' || sql%rowcount);   
    pk_log.debug('End add_function_id');
  exception 
    when others then
      PK_LOG.ERROR('Error in pk_dwh_fc2wp.add_function_id',sqlcode,sqlerrm);
      raise;                
  end;

  procedure upd_rel_flag_in_fc2wp_lists
  is
  begin
    pk_log.debug('Start upd_rel_flag_in_fc2wp_lists');

    update t_fc2wp_mapping fc2wp_mapping
       set in_icdm_a2l_flag = 'N'
     where in_icdm_a2l_flag = 'Y'
       and not exists( select 1
                         from t_dwh_fc2wp_rel_function rel_func
                            , t_fc2wp_definition        fc2wp
                            , t_fc2wp_def_version       fc2wp_vers
                        where rel_func.function_id      = fc2wp_mapping.function_id
                          and fc2wp_mapping.fcwp_ver_id = fc2wp_vers.fcwp_ver_id
                          and fc2wp.fcwp_def_id = fc2wp_vers.fcwp_def_id
                          and rel_func.division_id = fc2wp.div_value_id
                          and FC2WP_VERS.IN_WORK_FLAG = 'Y');

    update t_fc2wp_mapping fc2wp_mapping
       set in_icdm_a2l_flag = 'Y'
     where exists( select 1
                     from t_dwh_fc2wp_rel_function rel_func
                        , t_fc2wp_definition        fc2wp
                        , t_fc2wp_def_version       fc2wp_vers
                    where rel_func.function_id      = fc2wp_mapping.function_id
                      and fc2wp_mapping.fcwp_ver_id = fc2wp_vers.fcwp_ver_id
                      and fc2wp.fcwp_def_id = fc2wp_vers.fcwp_def_id
                      and rel_func.division_id = fc2wp.div_value_id
                      and FC2WP_VERS.IN_WORK_FLAG = 'Y');

    pk_log.trace('Updated function ID in t_fc2wp_mapping: ' || sql%rowcount);   
    pk_log.debug('End upd_rel_flag_in_fc2wp_lists');
  exception 
    when others then
      PK_LOG.ERROR('Error in pk_dwh_fc2wp.add_function_id',sqlcode,sqlerrm);
      raise;                
  end;
end;
/