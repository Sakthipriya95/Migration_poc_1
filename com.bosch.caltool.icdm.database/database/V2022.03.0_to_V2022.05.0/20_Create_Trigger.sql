spool c:\temp\02_Create_Trigger.log

--------------------------------------------------------
----ALM Task : 625676 - impl : Store Read-Only and Dependent params

-- Triggers for T_RVW_DEP_PARAMS
--------------------------------------------------------

--Insert Trigger
CREATE OR REPLACE TRIGGER TRG_A2L_DEP_PARAMS_INS 
  BEFORE INSERT ON T_A2L_DEP_PARAMS
  FOR EACH ROW
Begin
    IF :new.A2L_DEP_PARAM_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.A2L_DEP_PARAM_ID FROM DUAL;
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
ALTER TRIGGER TRG_A2L_DEP_PARAMS_INS ENABLE;

CREATE OR REPLACE TRIGGER TRG_A2L_DEP_PARAMS_UPD 
  BEFORE UPDATE ON T_A2L_DEP_PARAMS
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
ALTER TRIGGER TRG_A2L_DEP_PARAMS_UPD ENABLE;


spool off
