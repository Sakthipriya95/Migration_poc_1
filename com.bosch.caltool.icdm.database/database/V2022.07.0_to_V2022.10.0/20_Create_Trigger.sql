spool c:\temp\20_Create_Trigger.log


--------------------------------------------------------------------------------------------------------------
--ALM Task :689706 - Impl : Server side Changes –  Design new table for storing A2l_WP_RESP_Finished status 
--------------------------------------------------------------------------------------------------------------

CREATE OR REPLACE TRIGGER TRG_A2L_WP_RESP_STATUS_INS 
  BEFORE INSERT ON T_A2L_WP_RESPONSIBILITY_STATUS
  FOR EACH ROW
Begin
    IF :new.A2L_WP_RESP_STATUS_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.A2L_WP_RESP_STATUS_ID FROM DUAL;
    END IF;
    
    IF :new.Version IS NULL THEN
        :new.Version  := 1;
    END IF;

    IF :new.CREATED_DATE is null THEN
        :new.CREATED_DATE := sys_extract_utc(systimestamp);
    END IF;

    IF :new.CREATED_USER is null THEN
        :new.CREATED_USER := user;
    END IF;

END;
/

ALTER TRIGGER TRG_A2L_WP_RESP_STATUS_INS ENABLE;

CREATE OR REPLACE TRIGGER TRG_A2L_WP_RESP_STATUS_UPD
  BEFORE UPDATE ON T_A2L_WP_RESPONSIBILITY_STATUS
  FOR EACH ROW
BEGIN
    IF :new.Version = :old.Version THEN
        :new.Version := :old.Version + 1;
    END IF;

    IF :new.Modified_Date is null or NOT UPDATING('Modified_Date') then
        :new.Modified_Date := sys_extract_utc(systimestamp);
    END IF;

    IF :new.Modified_User is null THEN
        :new.Modified_User := user;
    END IF;

END;
/

ALTER TRIGGER TRG_A2L_WP_RESP_STATUS_UPD ENABLE;


spool off
