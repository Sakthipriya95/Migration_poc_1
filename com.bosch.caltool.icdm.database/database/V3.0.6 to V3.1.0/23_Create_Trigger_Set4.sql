spool 'C:\Temp\23_Create_Trigger_Set4.log'

--------------------------------------------
-- Other tables
-- Task 462615 - update in modified_date set
--------------------------------------------

--TRG_values_clear_inf_oth_upd
-- changed Modified_date's 'and' to 'or'
create or replace TRIGGER TRG_values_clear_inf_oth_upd
BEFORE UPDATE ON t_values_clear_inf_other_tools
FOR EACH ROW
BEGIN
    IF not updating('Modified_Date') or :new.Modified_Date is null THEN
        :new.Modified_Date := sys_extract_utc(systimestamp);
    END IF;

    IF :new.Modified_User is null THEN
        :new.Modified_User := user;
    END IF;
END;
/


--TRG_values_clear_mail_recp_UPD
-- changed Modified_date's 'and' to 'or'
create or replace TRIGGER TRG_values_clear_mail_recp_UPD
BEFORE UPDATE ON t_values_clear_mail_recp
FOR EACH ROW
BEGIN
    IF not updating('Modified_Date') or :new.Modified_Date is null THEN
        :new.Modified_Date := sys_extract_utc(systimestamp);
    END IF;

    IF :new.Modified_User is null THEN
        :new.Modified_User := user;
    END IF;
END;
/


--TRG_APPLICATION_LOG_UPD
-- added updating-modifieddate condition
create or replace TRIGGER TRG_APPLICATION_LOG_UPD 
  BEFORE UPDATE ON T_APPLICATION_LOG
  FOR EACH ROW
Begin

    IF :new.MODIFIED_DATE is null or NOT UPDATING('Modified_Date') then
        :new.MODIFIED_DATE := sys_extract_utc(systimestamp);
    END IF;

    IF :new.MODIFIED_USER is null THEN
        :new.MODIFIED_USER := user;
    END IF;

END;
/

--trg_rvw_mailhist_upd
-- added updating-modifieddate condition
create or replace TRIGGER trg_rvw_mailhist_upd 
  BEFORE UPDATE ON T_RVW_PARTICIPTS_MAIL_HIST
  FOR EACH ROW
BEGIN
    if :new.version = :old.version then
        :new.version := :old.version + 1;
    end if;

    if :new.modified_date is null or not updating('modified_date') then
        :new.modified_date := sys_extract_utc(systimestamp);
    end if;

    if :new.modified_user is null then 
        :new.modified_user := user;
    end if;
END;

/
 
--trg_rvw_mailsubscr_upd
-- removed updating-modifieduser
create or replace TRIGGER "trg_rvw_mailsubscr_upd" 
  BEFORE UPDATE ON t_rvw_participts_mail_subscr
  FOR EACH ROW
BEGIN
    if :new.version = :old.version then
        :new.version := :old.version + 1;
    end if;

    if :new.modified_date is null or not updating('modified_date') then
        :new.modified_date := sys_extract_utc(systimestamp);
    end if;

    if :new.modified_user is null then 
        :new.modified_user := user;
    end if;
END;
/

spool off
