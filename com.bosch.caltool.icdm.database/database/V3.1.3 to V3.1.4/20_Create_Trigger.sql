spool 'C:\Temp\20_Create_Trigger.log'

-------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 517702: Trigger for T_A2L_WP_IMPORT_PROFILE table  
------------------------------------------------------------------------------------------------------------------

CREATE OR REPLACE TRIGGER TRG_A2L_WP_PROFILE_INS
    BEFORE INSERT ON T_A2L_WP_IMPORT_PROFILE
    FOR EACH ROW

BEGIN
    IF :new.profile_id IS NULL THEN
        SELECT seqv_attributes.NEXTVAL
        INTO :new.profile_id
        FROM dual;
    END IF;

    IF :new.version IS NULL THEN
        :new.version := 1;
    END IF;

    IF :new.created_date IS NULL THEN
        :new.created_date := sys_extract_utc(systimestamp);
    END IF;

    IF :new.created_user IS NULL THEN
        :new.created_user := user;
    END IF;

END;
/


CREATE OR REPLACE TRIGGER TRG_A2L_WP_PROFILE_UPDT
    BEFORE UPDATE ON T_A2L_WP_IMPORT_PROFILE
    FOR EACH ROW

BEGIN
    IF :new.version = :old.version THEN
        :new.version := :old.version + 1;
    END IF;

    IF :new.modified_date IS NULL OR :new.modified_date = :old.modified_date THEN
        :new.modified_date := sys_extract_utc(systimestamp);
    END IF;

    IF :new.modified_user IS NULL THEN
        :new.modified_user := user;
    END IF;

END;
/

spool off
