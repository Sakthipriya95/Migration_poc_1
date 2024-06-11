spool c:\temp\30_Table_Data.log
----------------------------------------------------------
--Review 616939 : Questionnaires that have no questions to answer should appear as 'filled' in the overview and PIDC tree.
--
--Migration script to update the status of all the questionnaire responses working set
--which doesnt have any questions attached to it
----------------------------------------------------------
declare
    v_q_counter number;
    v_ver_counter number;
    
begin 
    FOR val in (select distinct qnaire_vers_id from T_RVW_QNAIRE_RESP_VERSIONS) 
    LOOP
        select count(*) into v_q_counter 
           from t_question 
           where QNAIRE_VERS_ID = val.qnaire_vers_id;
        select count(*) into v_ver_counter 
           from T_RVW_QNAIRE_RESP_VERSIONS 
           where QNAIRE_VERS_ID = val.qnaire_vers_id and NAME ='Working Set';
           
        if v_q_counter = 0 and v_ver_counter <> 0 
        then
            UPDATE T_RVW_QNAIRE_RESP_VERSIONS 
               SET QNAIRE_VERS_STATUS = 'P' 
               where QNAIRE_VERS_ID = val.qnaire_vers_id and NAME ='Working Set';
        end if;
       
    END LOOP;
end;
/

commit;

spool off