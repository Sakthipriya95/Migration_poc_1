spool c:\temp\12_Create_Trigger2.log


-- before insert T_RVW_RESULTS_SECONDARY

CREATE OR REPLACE TRIGGER TRG_RESULTS_SECONDARY_INS 
  BEFORE INSERT ON T_RVW_RESULTS_SECONDARY
  FOR EACH ROW
Begin
    IF :new.SEC_REVIEW_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.SEC_REVIEW_ID FROM DUAL;
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

-- before update T_RVW_RESULTS_SECONDARY
CREATE OR REPLACE TRIGGER TRG_RESULTS_SECONDARY_UPDT 
  BEFORE UPDATE ON T_RVW_RESULTS_SECONDARY
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

-- before insert T_RVW_PARAMETERS_SECONDARY
CREATE OR REPLACE TRIGGER TRG_RVW_PARAM_SECONDARY_INS 
  BEFORE INSERT ON T_RVW_PARAMETERS_SECONDARY
  FOR EACH ROW
Begin
    IF :new.SEC_RVW_PARAM_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.SEC_RVW_PARAM_ID FROM DUAL;
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
 
-- before update T_RVW_PARAMETERS_SECONDARY
CREATE OR REPLACE TRIGGER TRG_RVW_PARAM_SECONDARY_UPDT 
  BEFORE UPDATE ON T_RVW_PARAMETERS_SECONDARY
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

-------------------------------------------------
--Story 221726 Triggers for T_ALTERNATE_ATTR
----------------------------------------------------------

-- before insert T_ALTERNATE_ATTR
CREATE OR REPLACE TRIGGER TRG_ALTERNATE_ATTR_INS 
  BEFORE INSERT ON T_ALTERNATE_ATTR
  FOR EACH ROW
Begin
    IF :new.ATTR_PK is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.ATTR_PK FROM DUAL;
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

-- before update T_ALTERNATE_ATTR
CREATE OR REPLACE TRIGGER TRG_ALTERNATE_ATTR_UPDT 
  BEFORE UPDATE ON T_ALTERNATE_ATTR
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