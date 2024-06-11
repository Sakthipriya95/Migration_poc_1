spool C:\Temp\21_Create_Trigger_Qnaire.log


------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 567145: Create tables and Constrains based on ER diagram for questionnaire enhancements
------------------------------------------------------------------------------------------------------------------

-- create TRIGGER TRG_RVW_QNAIRE_RESP_VERS_INS

create TRIGGER TRG_RVW_QNAIRE_RESP_VER_INS 
  BEFORE INSERT ON t_rvw_qnaire_resp_versions
  FOR EACH ROW
Begin
    IF :new.qnaire_resp_vers_id is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.qnaire_resp_vers_id FROM DUAL;
    END IF;

    IF :new.VERSION is null THEN
        :new.VERSION := 1;
    END IF;

    IF :new.CREATED_DATE is null THEN
        :new.CREATED_DATE := sys_extract_utc(systimestamp);
    END IF;

    IF :new.CREATED_USER is null THEN
        :new.CREATED_USER := user;
    END IF;

END;
/

-- create TRIGGER TRG_RVW_QNAIRE_RESP_VERS_UPDT

create TRIGGER TRG_RVW_QNAIRE_RESP_VER_UPDT
    BEFORE UPDATE ON t_rvw_qnaire_resp_versions
    FOR EACH ROW
BEGIN
    IF :new.VERSION = :old.VERSION THEN
        :new.VERSION := :old.VERSION + 1;
    END IF;

    if :new.MODIFIED_DATE IS NULL or NOT UPDATING('Modified_Date') then
        :new.MODIFIED_DATE := sys_extract_utc(systimestamp);
    end if;

    if :new.MODIFIED_USER IS NULL then
        :new.MODIFIED_USER := user;
    end if;
 
END;
/


-- create TRIGGER TRG_RVW_QNAIRE_RESP_VAR_INS

create TRIGGER TRG_RVW_QNAIRE_RESP_VAR_INS 
  BEFORE INSERT ON t_rvw_qnaire_resp_variants
  FOR EACH ROW
Begin
    IF :new.qnaire_resp_var_id is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.qnaire_resp_var_id FROM DUAL;
    END IF;

    IF :new.VERSION is null THEN
        :new.VERSION := 1;
    END IF;

    IF :new.CREATED_DATE is null THEN
        :new.CREATED_DATE := sys_extract_utc(systimestamp);
    END IF;

    IF :new.CREATED_USER is null THEN
        :new.CREATED_USER := user;
    END IF;

END;
/

-- create TRIGGER TRG_RVW_QNAIRE_RESP_VAR_UPDT

create TRIGGER TRG_RVW_QNAIRE_RESP_VAR_UPDT
    BEFORE UPDATE ON t_rvw_qnaire_resp_variants
    FOR EACH ROW
BEGIN
    IF :new.VERSION = :old.VERSION THEN
        :new.VERSION := :old.VERSION + 1;
    END IF;

    if :new.MODIFIED_DATE IS NULL or NOT UPDATING('Modified_Date') then
        :new.MODIFIED_DATE := sys_extract_utc(systimestamp);
    end if;

    if :new.MODIFIED_USER IS NULL then
        :new.MODIFIED_USER := user;
    end if;
 
END;
/


spool off
