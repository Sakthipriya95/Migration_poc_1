spool c:\temp\95_DB_Jobs_DGS_ICDM_JPA_ARCHIVAL_Schema.log

--------------------------------------------------------------------------------------------------------------------------------
--  ALM Task : 724121 - DataAssessment File Status Reset from Inprogess to Failed using DB Jobs
--  Job has to be created on DGS_ICDM_JPA_ARCHIVAL_Schema
--------------------------------------------------------------------------------------------------------------------------------

BEGIN
    DBMS_SCHEDULER.CREATE_JOB (
            job_name => '"DGS_ICDM_ARCHIVAL"."FILE_ARCHIVAL_STATUS_JOB"',
            job_type => 'PLSQL_BLOCK',
            job_action => 'begin 
                           update t_da_data_assessment
                           SET FILE_ARCHIVAL_STATUS = ''F''
                                WHERE DATA_ASSESSMENT_ID in (select DATA_ASSESSMENT_ID 
                                                                from t_da_data_assessment 
                                                                where file_archival_status = ''I'' 
                                                                AND CREATED_DATE < sys_extract_utc(systimestamp - interval ''1'' hour));
                           end;',
            number_of_arguments => 0,
            start_date => NULL,
            repeat_interval => 'FREQ=HOURLY',
            end_date => NULL,
                job_class => '"SYS"."DBMS_JOB$"',
            enabled => FALSE,
            auto_drop => FALSE,
            comments => 'Job to Update the Status of Failed File Archival');

    DBMS_SCHEDULER.SET_ATTRIBUTE( 
             name => '"DGS_ICDM_ARCHIVAL"."FILE_ARCHIVAL_STATUS_JOB"', 
             attribute => 'logging_level', value => DBMS_SCHEDULER.LOGGING_OFF);
  
    DBMS_SCHEDULER.enable(
             name => '"DGS_ICDM_ARCHIVAL"."FILE_ARCHIVAL_STATUS_JOB"');
END;


spool off